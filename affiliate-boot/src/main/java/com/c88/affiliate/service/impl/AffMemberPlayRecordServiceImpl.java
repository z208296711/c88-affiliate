package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.mapper.AffMemberTransactionsMapper;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.SearchMemberDepositRecordForm;
import com.c88.affiliate.pojo.form.SearchMemberPlayingRecordForm;
import com.c88.affiliate.pojo.vo.H5MemberPlayingRecordVO;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IMemberPlayRecordService;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.web.util.AffiliateUtils;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.game.adapter.dto.GameVO;
import com.c88.member.api.MemberFeignClient;
import com.c88.member.dto.MemberInfoDTO;
import com.c88.payment.client.MemberRechargeClient;
import com.c88.payment.vo.MemberDepositDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class AffMemberPlayRecordServiceImpl implements IMemberPlayRecordService {

    private final AffMemberTransactionsMapper affMemberTransactionsMapper;

    private final MemberFeignClient memberFeignClient;

    private final MemberRechargeClient memberRechargeClient;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final AffAffiliateCommissionRecordServiceImpl affAffiliateCommissionRecordService;

    private final GameFeignClient gameFeignClient;

    @Override
    public IPage<H5MemberPlayingRecordVO> findMemberPlayRecord(SearchMemberPlayingRecordForm form) {
        form.setStartTime(toLocalDateTime(form.getBetStartTime(), true));
        form.setEndTime(toLocalDateTime(form.getBetEndTime(), false));
        Long affiliateId = AffiliateUtils.getAffiliateId();
        List<Long> parentIds = affAffiliateCommissionRecordService.findTeamAffiliate(affiliateId, null);
        parentIds.add(affiliateId);
        form.setParentIds(parentIds);
        IPage<H5MemberPlayingRecordVO> result = form.getStatus() > 0
                ? affMemberTransactionsMapper.findMemberPlayRecord(new Page(form.getPageNum(), form.getPageSize()), form)
                : affMemberTransactionsMapper.findMemberPlayRecordWithoutStatus(new Page(form.getPageNum(), form.getPageSize()), form);
        List<H5MemberPlayingRecordVO> voX = result.getRecords();

        List<String> userNameX = voX.stream().map(x -> x.getUsername()).collect(Collectors.toList());
        Result<List<MemberInfoDTO>> memberInfoX = memberFeignClient.getMemberInfoByUsernames(userNameX);

        List<String> platformCodeX = voX.stream().map(x -> x.getPlatformCode()).collect(Collectors.toList());
        Map<String, List<GameVO>> gameListByPlatforms = gameFeignClient.getGameListByPlatforms(platformCodeX).getData();

        List<H5MemberPlayingRecordVO> resultX = voX.stream().map(x -> {
            Optional<MemberInfoDTO> info = memberInfoX.getData().stream().filter(member -> member.getUsername().equals(x.getUsername())).findFirst();
            info.ifPresent(member -> x.setVipName(member.getCurrentVipName()));
            //電子類
            if(null != gameListByPlatforms.get(x.getPlatformCode())) {
                Optional<GameVO> optionalGameVO = gameListByPlatforms.get(x.getPlatformCode()).stream().filter(gameVo -> gameVo.getId().equals(x.getGameId())).findFirst();
                optionalGameVO.ifPresent(gameVo -> x.setGameName(gameVo.getName()));
            }
            return x;
        }).collect(Collectors.toList());
        return result.setRecords(resultX);
    }

    private LocalDateTime toLocalDateTime(String inDate, boolean begin) {
        LocalDateTime result;
        LocalDate ld = Optional.ofNullable(inDate).map(LocalDate::parse).orElse(LocalDate.now());
        LocalDateTime ldt = ld.atStartOfDay();
        result = begin ? ldt : ldt.with(LocalTime.MAX);
        return result;
    }

    @Override
    public PageResult<MemberDepositDTO> findMemberDepositRecord(SearchMemberDepositRecordForm form) {
        LocalDateTime start = toLocalDateTime(form.getDepositStartTime(), true);
        LocalDateTime end = toLocalDateTime(form.getDepositEndTime(), false);
        Long affiliateId = AffiliateUtils.getAffiliateId();
        AffiliateMember member = iAffiliateMemberService.findAffiliateIdByMember(form.getUsername());
        if (!Arrays.asList(member.getParents().split(",")).contains(affiliateId.toString())) {
            return null;
        }

        return memberRechargeClient.findMemberRecharges(form.getUsername(),
                start,
                end,
                form.getPageNum(),
                form.getPageSize());
    }
}
