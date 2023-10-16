package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.c88.affiliate.mapper.AffMemberTransactionsMapper;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.SearchAffiliateWinLossForm;
import com.c88.affiliate.pojo.form.SearchMemberWinLossForm;
import com.c88.affiliate.pojo.vo.AffiliateWinLossVO;
import com.c88.affiliate.pojo.vo.MemberWinLossVO;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.affiliate.service.*;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.mybatis.util.PageUtil;
import com.c88.common.web.exception.BizException;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.game.adapter.dto.MemberRebateRecordDTO;
import com.c88.payment.client.AffiliateBalanceChangeRecordClient;
import com.c88.payment.client.MemberBalanceClient;
import com.c88.payment.dto.AffiliateBalanceChangeRecordDTO;
import com.c88.payment.dto.MemberBalanceInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: TODO
 * @author: marcoyang
 * @date: 2022/12/26
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliateMemberWinLossServiceImpl implements IAffiliateMemberWinLossService {

    private final IMemberTransactionsRepository iMemberTransactionsRepository;

    private final AffMemberTransactionsMapper affMemberTransactionsMapper;
    private final IAffMemberTransactionsService affMemberTransactionsService;

    private final IAffiliateService iAffiliateService;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final MemberBalanceClient memberBalanceClient;

    private final AffiliateBalanceChangeRecordClient affiliateBalanceChangeRecordClient;

    private final GameFeignClient gameFeignClient;
    private final IAffAffiliateCommissionRecordService iAffAffiliateCommissionRecordService;

    @Override
    public IPage<AffiliateWinLossVO> getAffiliateWinLoss(SearchAffiliateWinLossForm form) {
        List<Affiliate> affiliateX = findTeamAffiliate(form.getUsername());
        List<AffiliateWinLossVO> dataX = Optional.ofNullable(affiliateX).map(x -> {
            List<Long> affiliates = getAffiliateX.apply(x);
            Result<List<AffiliateBalanceChangeRecordDTO>> retAffiliateBalance = affiliateBalanceChangeRecordClient.findBalanceChangeFromDate(affiliates, form.getStartTime(), form.getEndTime());
            Map<Long, List<AffiliateBalanceChangeRecordDTO>> affiliateBalanceMap =
                    Result.isSuccess(retAffiliateBalance) ? getAffiliateMap.apply(retAffiliateBalance.getData()) : getAffiliateMap.apply(List.of());
            return x.stream().map(a -> getAffiliateWinLossVO(form, a, affiliateBalanceMap)).collect(Collectors.toList());
        }).orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        return PageUtil.toIPage(dataX, form.getPageNum(), form.getPageSize());
    }

    private List<Affiliate> findTeamAffiliate(String affiliateName) {
        Affiliate inAffiliate = iAffiliateService.lambdaQuery().eq(Affiliate::getUsername, affiliateName).eq(Affiliate::getEnable, 1)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        if (inAffiliate.getLevel() == 1) {
            return iAffiliateService.lambdaQuery()
                    .eq(Affiliate::getParentUsername, affiliateName).eq(Affiliate::getEnable, 1)
                    .orderByAsc(Affiliate::getId).list();
        }

        return List.of(inAffiliate);
    }

    private AffiliateWinLossVO getAffiliateWinLossVO(SearchAffiliateWinLossForm form, Affiliate affiliate, Map<Long, List<AffiliateBalanceChangeRecordDTO>> dtoMap) {
        LocalDateTime start = form.getStartTime();
        LocalDateTime end = form.getEndTime();
        Long affiliateId = affiliate.getId();
        BigDecimal affBalanceFromAdmin = Optional.ofNullable(dtoMap.get(affiliateId))
                .map(x -> x.stream().map(AffiliateBalanceChangeRecordDTO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
        log.info("WLReport fromAdmin {}", affBalanceFromAdmin);
        List<AffiliateMember> registerMembers = getMemberByAgentIdDate(affiliateId, start, end);
        List<AffiliateMember> members = getMemberByAgentId(affiliateId);
        List<MemberBalanceInfoDTO> balanceX = getMemberBalanceByAgentId(affiliateId, members);
        List<MemberBalanceInfoDTO> firstChargeMemberX = balanceX.stream()
                .filter(filter -> Objects.nonNull(filter.getFirstRechargeTime()))
                .collect(Collectors.toList());

        List<MemberBalanceInfoDTO> periodFirstChargeMemberX = firstChargeMemberX.stream()
                .filter(f -> f.getFirstRechargeTime().isAfter(start) && f.getFirstRechargeTime().isBefore(end))
                .collect(Collectors.toList());
        BigDecimal periodAmount = periodFirstChargeMemberX.stream()
                .map(MemberBalanceInfoDTO::getFirstRechargeBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, BigDecimal> rebateMap = gameFeignClient.findRebateRecordMember(form.getStartTime().toString(), form.getEndTime().toString()).getData()
                .stream().collect(Collectors.groupingBy(MemberRebateRecordDTO::getMemberId,
                        Collectors.reducing(BigDecimal.ZERO, MemberRebateRecordDTO::getRebate, BigDecimal::add)));
        BigDecimal rebate = members.stream().map(AffiliateMember::getMemberId).map(rebateMap::get).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        AffiliateWinLossVO vo = iMemberTransactionsRepository.findByParentIdWinLoss(affiliateId, form.getStartTime(), form.getEndTime());

        vo.setNetProfit(vo.getNetProfit());
        vo.setUsername(affiliate.getUsername());
        vo.setAgentLevel(affiliate.getLevel());
        vo.setRegisterNum(registerMembers.size());
        vo.setDownPaymentNum(firstChargeMemberX.size());
        vo.setPeriodDepositNum(periodFirstChargeMemberX.size());
        vo.setPeriodDepositAmount(periodAmount);
        vo.setRebate(rebate);
        return vo;
    }

    private List<AffiliateMember> getMemberByAgentId(Long agentId) {
        return iAffiliateMemberService.lambdaQuery()
                .eq(AffiliateMember::getParentId, agentId)
                .list();
    }

    private List<AffiliateMember> getMemberByAgentIdDate(Long agentId, LocalDateTime start, LocalDateTime end) {
        return iAffiliateMemberService.lambdaQuery()
                .eq(AffiliateMember::getParentId, agentId)
                .between(AffiliateMember::getRegisterTime, start, end)
                .list();
    }

    private List<MemberBalanceInfoDTO> getMemberBalanceByAgentId(Long agentId, List<AffiliateMember> members) {

        List<MemberBalanceInfoDTO> memberBalanceInfos = Collections.emptyList();
        Result<List<MemberBalanceInfoDTO>> memberBalanceInfoResult = memberBalanceClient
                .findMemberBalanceInfoByIds(members.stream().map(AffiliateMember::getMemberId).collect(Collectors.toList()));
        if (Result.isSuccess(memberBalanceInfoResult)) {
            memberBalanceInfos = memberBalanceInfoResult.getData();
        }
        return memberBalanceInfos;
//        long firstRechargeMemberCount = members.stream()
//                .filter(filter -> Objects.equals(filter.getParentId(), agentId) && balanceMemberId.contains(filter.getMemberId()))
//                .count();
    }

    Function<List<Affiliate>, List<Long>> getAffiliateX = x -> x.stream().map(Affiliate::getId).collect(Collectors.toList());

    Function<List<AffiliateBalanceChangeRecordDTO>, Map<Long, List<AffiliateBalanceChangeRecordDTO>>> getAffiliateMap =
            x -> x.isEmpty() ? Collections.emptyMap() : x.stream().collect(Collectors.groupingBy(AffiliateBalanceChangeRecordDTO::getAffiliateId));

    @Override
    public IPage<MemberWinLossVO> getMemberWinLoss(SearchMemberWinLossForm form) {

        iMemberTransactionsRepository.findByDateWinLoss("", form.getStartTime(), form.getEndTime());
        return null;
    }
}
