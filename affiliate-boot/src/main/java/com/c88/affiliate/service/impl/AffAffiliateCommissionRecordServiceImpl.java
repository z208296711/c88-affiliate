package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.converter.AffMemberTransactionsConverter;
import com.c88.affiliate.converter.AffiliateCommissionRecordConverter;
import com.c88.affiliate.enums.AffiliateCommissionStateEnum;
import com.c88.affiliate.enums.AffiliateCommissionStatusEnum;
import com.c88.affiliate.enums.AffiliateCommissionTotalStateEnum;
import com.c88.affiliate.mapper.AffAffiliateCommissionPlatformRecordMapper;
import com.c88.affiliate.mapper.AffAffiliateCommissionRecordMapper;
import com.c88.affiliate.mapper.AffAffiliateCommissionTotalRecordMapper;
import com.c88.affiliate.mapper.AffMemberTransactionsMapper;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionPlatformRecord;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionTotalRecord;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.FindPerformanceReportExportForm;
import com.c88.affiliate.pojo.form.FindPerformanceReportForm;
import com.c88.affiliate.pojo.form.SearchAffiliateTeamTransForm;
import com.c88.affiliate.pojo.vo.AffTransactionCountVO;
import com.c88.affiliate.pojo.vo.AffiliateCommissionTeamReportVO;
import com.c88.affiliate.pojo.vo.AffiliatePlatformDetailVO;
import com.c88.affiliate.pojo.vo.AffliateCommissionPersonReportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportExportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportVO;
import com.c88.affiliate.service.IAffAffiliateCommissionRecordService;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.affiliate.service.ICommissionGroupService;
import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.DateUtil;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import com.c88.common.web.util.UUIDUtils;
import com.c88.game.adapter.api.GameFeignClient;
import com.c88.game.adapter.api.PlatformClient;
import com.c88.game.adapter.dto.MemberRebateRecordDTO;
import com.c88.game.adapter.dto.PlatformDTO;
import com.c88.payment.dto.AddAffiliateBalanceDTO;
import com.c88.payment.enums.AffiliateBalanceChangeTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mac
 * @description 针对表【aff_affiliate_commission_record】的数据库操作Service实现
 * @createDate 2022-12-08 14:25:05
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AffAffiliateCommissionRecordServiceImpl extends ServiceImpl<AffAffiliateCommissionRecordMapper, AffAffiliateCommissionRecord>
        implements IAffAffiliateCommissionRecordService {

    private final IAffMemberTransactionsService affMemberTransactionsService;

    private final IAffiliateService affiliateService;

    private final AffMemberTransactionsMapper affMemberTransactionsMapper;

    private final ICommissionGroupService commissionGroupService;

    private final AffAffiliateCommissionRecordMapper affAffiliateCommissionRecordMapper;

    private final AffAffiliateCommissionTotalRecordMapper affAffiliateCommissionTotalRecordMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final PlatformClient platformClient;

    private final AffAffiliateCommissionPlatformRecordMapper affAffiliateCommissionPlatformRecordMapper;

    private final IAffAffiliateCommissionPlatformRecordServiceImpl affAffiliateCommissionPlatformRecordService;

    private final IAffiliateMemberService iAffiliateMemberService;
//    private final AffiliateMapper affiliateMapper;

    private final AffMemberTransactionsConverter affMemberTransactionsConverter;

    private final AffiliateCommissionRecordConverter affiliateCommissionRecordConverter;

    private final GameFeignClient gameFeignClient;

    @Override
    public int createCommission(int level, String date) {
        LocalDateTime floor = DateUtil.strToFirstDayOfMonthWithLocalDateTime(date);
        LocalDateTime ceiling = floor.plusMonths(1);

        //所有該層代理
        List<Long> agentIds = affiliateService.lambdaQuery().eq(Affiliate::getLevel, level).list().stream().map(Affiliate::getId).collect(Collectors.toList());

        List<AffAffiliateCommissionRecord> list = new ArrayList<>();
//        List<Affiliate> affList = new ArrayList<>();
        List<AffAffiliateCommissionPlatformRecord> platformRecords = new ArrayList<>();
        for (Long agentId : agentIds) {
            AffAffiliateCommissionRecord commissionRecord = this.countPersonalCommission(agentId, date);
            if (commissionRecord == null) continue;

//            Affiliate affiliate = affiliateService.lambdaQuery().eq(Affiliate::getId, agentId).one();
//            List<Long> agents = Arrays.asList(affiliate.getParents().split(",")).stream().mapToLong(num->Long.parseLong(num)).boxed().collect(Collectors.toList());

            List<Long> agents = affiliateService.getSubAffiliateWithSelfById(agentId).stream().mapToLong(Affiliate::getId).boxed().collect(Collectors.toList());
            //帳單明細
            List<AffiliatePlatformDetailVO> platformDetail = this.getPlatformDetail(agents, date);
            platformDetail.forEach(platform -> {
                AffAffiliateCommissionPlatformRecord platformRecord = AffAffiliateCommissionPlatformRecord.builder()
                        .agentId(agentId)
                        .platformName(platform.getPlatformName())
                        .issueDate(date)
                        .platformFee(platform.getPlatformFee())
                        .totalWinloss(platform.getWinLoss())
                        .amount(platform.getWinLoss().subtract(platform.getPlatformFee())).build();
                platformRecords.add(platformRecord);
            });
            BigDecimal allPlatformFee = platformDetail.stream().map(AffiliatePlatformDetailVO::getPlatformFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            commissionRecord.setPlatformFee(allPlatformFee);
            list.add(commissionRecord);
        }

//        affiliateService.saveOrUpdateBatch(affList);
        affAffiliateCommissionRecordMapper.insertBatch(list);
        if (platformRecords.size() > 0)
            affAffiliateCommissionPlatformRecordMapper.insertBatch(platformRecords);

        //同時新增一筆至總表
        affAffiliateCommissionTotalRecordMapper.insert(AffAffiliateCommissionTotalRecord.builder().level(level).verifyDate(date).status(AffiliateCommissionTotalStateEnum.NOT_YET.getCode()).build());

        return list.size();
    }

    private AffAffiliateCommissionRecord countPersonalCommission(Long agentId, String date) {
        LocalDateTime floor = DateUtil.strToFirstDayOfMonthWithLocalDateTime(date);
        LocalDateTime ceiling = floor.plusMonths(1);
        //https://smartclouds.atlassian.net/browse/HCM-443 被凍結的代理需產生佣金審核單
        Affiliate affiliate = affiliateService.lambdaQuery().eq(Affiliate::getId, agentId).one();
        if (affiliate == null) return null;
//        List<Affiliate> affiliates = affiliateService.lambdaQuery().likeRight(Affiliate::getParents, affiliate.getId() + ",").list();
//        List<Long> agents = affiliates.stream().mapToLong(Affiliate::getId).boxed().collect(Collectors.toList());
//        agents.add(affiliate.getId());
        List<Long> agents = affiliateService.getSubAffiliateWithSelfById(agentId).stream().mapToLong(Affiliate::getId).boxed().collect(Collectors.toList());

        QueryWrapper<AffTransactionCountVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", agents);
        queryWrapper.ge("gmt_create", floor);
        queryWrapper.lt("gmt_create", ceiling);
        queryWrapper.ne("type",4);
        AffTransactionCountVO affTransactionCountVO = Optional.ofNullable(affMemberTransactionsMapper.countAffTransaction(queryWrapper)).orElse(new AffTransactionCountVO());

        return toAffAffiliateCommissionRecord(agentId, date, affTransactionCountVO);
    }

    private AffAffiliateCommissionRecord toAffAffiliateCommissionRecord(Long agentId, String date, AffTransactionCountVO affTransactionCountVO) {
        Affiliate affiliate = affiliateService.lambdaQuery().eq(Affiliate::getId, agentId).one();
        if (Objects.isNull(affiliate)) throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);

//        List<Affiliate> affiliates = affiliateService.lambdaQuery().likeRight(Affiliate::getParents, affiliate.getId() + ",").list();
//        List<Long> agents = affiliates.stream().mapToLong(Affiliate::getId).boxed().collect(Collectors.toList());
//        agents.add(affiliate.getId());

        List<Long> agents = affiliateService.getSubAffiliateWithSelfById(agentId).stream().mapToLong(Affiliate::getId).boxed().collect(Collectors.toList());

        BigDecimal winLoss = affTransactionCountVO.getWinLoss().negate();//負贏利
//        BigDecimal platformFee = affTransactionCountVO.getPlatformFee() ;
        BigDecimal awardAmount = affTransactionCountVO.getAwardAmount().setScale(2, RoundingMode.DOWN);
        BigDecimal rechargeAwardAmount = affTransactionCountVO.getRechargeAwardAmount().setScale(2, RoundingMode.DOWN);

        Integer status = AffiliateCommissionStatusEnum.VERIFYING.getCode();
        Integer state = AffiliateCommissionStateEnum.NOT_YET.getCode();

        //下級輸贏總和
//        BigDecimal subWinLoss = getSubWinLoss(agentId, date);
//        BigDecimal totalWinloss = winLoss.add(subWinLoss);

        //場館費
        List<AffiliatePlatformDetailVO> platformDetail = getPlatformDetail(agents, date);
        BigDecimal platformFee = platformDetail.stream().map(AffiliatePlatformDetailVO::getPlatformFee).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.DOWN);
        platformFee = winLoss.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : platformFee;
        //淨輸贏=總输赢-會員紅利-會員反水-場館費-微調扣除金額(TODO)
        BigDecimal netWinLoss = winLoss.subtract(awardAmount).subtract(rechargeAwardAmount)
                .subtract(platformFee).subtract(BigDecimal.ZERO).setScale(2, RoundingMode.DOWN);

        //前期缺額(負數)
//        Affiliate affiliate = affiliateService.lambdaQuery().eq(Affiliate::getId, agentId).oneOpt().orElse(null);

        //上期缺額
        BigDecimal lastDifference = affiliate.getDifference();

        //佣金比例
        Long commissionGroupId = affiliate.getCommissionGroupId();
        //活躍人數
        int activeMember = affMemberTransactionsService.countActiveMember(agents, date);
        BigDecimal commissionRate = commissionGroupService.getCommissionRate(commissionGroupId, activeMember, winLoss);
        boolean isPass = true;
        //這裡的-1是為了區分佣金比例為什麼是0，如果是-1，再把commissionRate還原為0，以不影響後面的計算
        if (Objects.equals(commissionRate, new BigDecimal(-1))) {
            status = AffiliateCommissionStatusEnum.ACTIVE_MEMBER_NOT_ENOUGH.getCode();
//            state = AffiliateCommissionStateEnum.ISSUED.getCode();
            isPass = false;
            commissionRate = BigDecimal.ZERO;
        }

        log.info("agent:{}, rate:{}", agentId, commissionRate);
        //本期代理佣金(有淨贏才有佣金)
        BigDecimal currentCommission = netWinLoss.compareTo(BigDecimal.ZERO) > 0 ? netWinLoss.multiply(commissionRate.divide(new BigDecimal(100))).setScale(2, RoundingMode.DOWN) : BigDecimal.ZERO;

        //全部佣金(減所有缺額)
        BigDecimal total = currentCommission.add(lastDifference);
        if (isPass && total.compareTo(BigDecimal.ZERO) <= 0) {
            status = AffiliateCommissionStatusEnum.UNDER_ISSUE.getCode();
//            state = AffiliateCommissionStateEnum.ISSUED.getCode();
        }
        //會員行銷費用
        BigDecimal marketingFee = awardAmount.add(rechargeAwardAmount);

//        }
        //淨贏利 = 會員虧損負盈利- 會員行銷費用-場館費
        BigDecimal netProfit = winLoss.subtract(marketingFee).subtract(platformFee).setScale(2, RoundingMode.DOWN);
        //本期缺額，如果負贏利為正，則減行銷費用及場館費，如果還為正則本期缺額為0，否則產生缺額
        // 2022/12/22 2:00 PM 此邏輯與Tony及Oakley確認
        BigDecimal currentDifference = winLoss.compareTo(BigDecimal.ZERO) > 0 ? (netProfit.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ZERO : netProfit) : winLoss;

        return AffAffiliateCommissionRecord.builder()
                .agentId(affiliate.getId())
                .agentUsername(affiliate.getUsername())
                .parentId(affiliate.getParentId())
                .verifyDate(date)
                .activeMembers(activeMember)
                .memberWinloss(winLoss)
                .marketingFee(marketingFee)
                .platformFee(platformFee)
                .netProfit(netProfit)
                .commissionRate(commissionRate)
                .currentCommission(currentCommission)
                .status(status)
                .totalDifference(lastDifference)
                .difference(currentDifference)
                .totalCommission(total.compareTo(BigDecimal.ZERO) > 0 ? total : BigDecimal.ZERO)
                .level(affiliate.getLevel())
                .parentUsername(affiliate.getParentUsername())
                .state(state)
                .awardAmount(awardAmount)
                .rechargeAwardAmount(rechargeAwardAmount)
                .lastDifference(lastDifference)
                .verifyStatus(AffiliateCommissionStatusEnum.VERIFYING.getCode())
                .build();
    }

    @Override
    public AffliateCommissionPersonReportVO getPersonalCommission(String date) {
        Long agentId = AffiliateUtils.getAffiliateId();

        //如果DB有，直接從DB return
        AffAffiliateCommissionRecord record = this.lambdaQuery()
                .eq(AffAffiliateCommissionRecord::getVerifyDate, date)
                .eq(AffAffiliateCommissionRecord::getAgentId, agentId)
                .one();
        if (record == null) {
            return null;
//            record = this.countPersonalCommission(agentId, date);
        }

        BigDecimal netWinLoss = getNetWinLoss(record);

        return AffliateCommissionPersonReportVO.builder()
                .verifyDate(date)
                .activeMembers(record.getActiveMembers())
                .validMembers(0)
                .totalWinLoss(record.getMemberWinloss())
                .platformFee(record.getPlatformFee())
                .awardAmount(record.getAwardAmount())
                .rechargeAwardAmount(record.getRechargeAwardAmount())
                .memberAdjust(BigDecimal.ZERO)
                .netProfit(record.getNetProfit()) //淨輸贏
                .lastDifference(record.getLastDifference())
                .netWinLoss(netWinLoss)//沖正後淨輸贏
                .commissionRate(record.getCommissionRate())
                .agentAdjust(BigDecimal.ZERO)
                .totalCommission(record.getTotalCommission())
                .state(record.getState())
                .issueDate(record.getIssueDate())
                .status(record.getStatus())
                .build();
    }

    private static BigDecimal getNetWinLoss(AffAffiliateCommissionRecord record) {
        //沖正後淨輸贏要另外看總輸贏是否為正，如果為正，則扣除行銷費用及場館費;反之，則直接用輸贏加上上期缺額
        BigDecimal netWinLoss = BigDecimal.ZERO;
        if (record.getMemberWinloss().compareTo(BigDecimal.ZERO) > 0) {
            netWinLoss = record.getMemberWinloss()
                    .subtract(record.getRechargeAwardAmount())
                    .subtract(record.getAwardAmount()).
                    subtract(record.getPlatformFee())
                    .add(record.getLastDifference()).setScale(2, RoundingMode.DOWN);
        } else {
            netWinLoss = record.getMemberWinloss().add(record.getLastDifference()).setScale(2, RoundingMode.DOWN);
            //
        }
        return netWinLoss;
    }

    @Override
    public PageResult<AffiliateCommissionTeamReportVO> getTeamCommission(SearchAffiliateTeamTransForm form) {
        Long agentId = AffiliateUtils.getAffiliateId();
        Affiliate belowAff = Optional.ofNullable(form.getAgentNameBelow())
                .map(x -> affiliateService.lambdaQuery().eq(Affiliate::getUsername, form.getAgentNameBelow()).one())
                .orElse(null);
        Map<String, Boolean> IsLevelOne = new HashMap<>();
        List<Long> agentIdX = findTeamAffiliate(agentId, IsLevelOne);
        if (belowAff != null && !agentIdX.contains(belowAff.getId())) {
            throw new BizException(ResultCode.MEMBER_NOT_EXIST_AFFILIATE_BELOW);
        } else if (belowAff != null) {
            List<Long> tmp = new ArrayList<>();
            tmp.add(belowAff.getId());
            agentIdX = tmp;
        }

        agentIdX.remove(agentId);
        agentIdX.add(0L);

        //查詢各級佣金發放狀態
        List<AffAffiliateCommissionTotalRecord> totalRecords = affAffiliateCommissionTotalRecordMapper
                .selectList(new LambdaQueryWrapper<AffAffiliateCommissionTotalRecord>().eq(AffAffiliateCommissionTotalRecord::getVerifyDate, form.getDate()));
        Long level1 = totalRecords.stream().filter(x -> x.getLevel().equals(1) && x.getStatus().equals(2)).count();
        Long level2 = totalRecords.stream().filter(x -> x.getLevel().equals(2) && x.getStatus().equals(2)).count();
        List<AffiliateMember> members = iAffiliateMemberService.lambdaQuery()
                .eq(AffiliateMember::getMasterId, agentId)
                .or()
                .eq(AffiliateMember::getParentId, agentId)
                .list();

        List<AffAffiliateCommissionRecord> agentBelowX = level2 != 0 ? this.lambdaQuery()
                .eq(AffAffiliateCommissionRecord::getVerifyDate, form.getDate())
                .in(AffAffiliateCommissionRecord::getAgentId, agentIdX).list() : List.of();

        AffAffiliateCommissionRecord agentData = level1 != 0 ? this.lambdaQuery()
                .eq(AffAffiliateCommissionRecord::getVerifyDate, form.getDate())
                .eq(AffAffiliateCommissionRecord::getAgentId, agentId).one() : null;

        AffiliateCommissionTeamReportVO vTotal = null;
        List<AffiliateCommissionTeamReportVO> agentTeamReportX = agentBelowX.stream().map(a -> toAffiliateCommissionTeamReportVO(a, members, agentId)).collect(Collectors.toList());
        vTotal = !Objects.isNull(agentData) ? toAffiliateCommissionTeamReportVO(agentData, members, agentId) : null;

        IPage<AffiliateCommissionTeamReportVO> datas = new Page<AffiliateCommissionTeamReportVO>(form.getPageNum(), form.getPageSize()).setRecords(agentTeamReportX);

        return PageResult.success(datas, Objects.isNull(vTotal) ? List.of() : List.of(vTotal));
    }

    private AffiliateCommissionTeamReportVO toAffiliateCommissionTeamReportVO(AffAffiliateCommissionRecord record, List<AffiliateMember> members, long agentId) {
        AffiliateCommissionTeamReportVO vo = new AffiliateCommissionTeamReportVO();
        if (record.getAgentId().equals(agentId)) {
            vo.setAgentUsername("");
            vo.setBottomMembers((long) members.size());
        } else {
            vo.setBottomMembers(members.stream()
                    .filter(o -> Objects.equals(o.getParentId(), record.getAgentId()))
                    .count());
            vo.setAgentUsername(record.getAgentUsername());
        }
        vo.setAgentId(record.getAgentId());
        vo.setActiveMembers(record.getActiveMembers());
        vo.setTotalCommission(record.getTotalCommission());
        vo.setCurrentCommission(record.getTotalCommission());
        vo.setNetProfit(record.getNetProfit());
        vo.setLastDifference(record.getLastDifference());
        vo.setNetWinLoss(getNetWinLoss(record));
        vo.setCommissionRate(record.getCommissionRate());
        return vo;
    }

    //    @Override
    private List<AffiliatePlatformDetailVO> getPlatformDetail(List<Long> agents, String date) {
//        Long agentId = AffiliateUtils.getAffiliateId();
//        if (agents == null) agentId = AffiliateUtils.getAffiliateId();
//        Affiliate affiliate = affiliateService.lambdaQuery().in(Affiliate::getId, agentId).one();
//        List<Long> agents = Arrays.asList(affiliate.getParents().split(",")).stream().mapToLong(num->Long.parseLong(num)).boxed().collect(Collectors.toList());

        LocalDateTime floor = DateUtil.strToFirstDayOfMonthWithLocalDateTime(date);
        LocalDateTime ceiling = floor.plusMonths(1);

        // 取得平台
        Result<List<PlatformDTO>> platformResult = platformClient.findAllPlatformDTO();
        Map<String, PlatformDTO> platformNameMap = new HashMap<>();
        if (Result.isSuccess(platformResult)) {
            platformNameMap = platformResult.getData()
                    .stream()
                    .collect(Collectors.toMap(PlatformDTO::getCode, Function.identity()));
        }

        QueryWrapper<AffiliatePlatformDetailVO> queryWrapper = new QueryWrapper<AffiliatePlatformDetailVO>();
        //where parent_id in (${agents}) and (gmt_create>=#{startTime} and gmt_create<#{endTime}) and type=3  group by platform_code")
        queryWrapper.in("parent_id", agents);
        queryWrapper.ge("gmt_create", floor);
        queryWrapper.lt("gmt_create", ceiling);
        queryWrapper.eq("type", 3);
        queryWrapper.groupBy("platform_code");
        List<AffiliatePlatformDetailVO> platformDetailReport = affMemberTransactionsMapper.getPlatformDetailReport(queryWrapper);

        Map<String, PlatformDTO> finalPlatformNameMap = platformNameMap;
        //算場館費
        platformDetailReport.forEach(detail -> {
            BigDecimal rate = finalPlatformNameMap.get(detail.getPlatformCode()).getRate();
            log.info("platform:{}, rate:{}, winloss:{}", detail.getPlatformCode(), rate, detail.getWinLoss());
            if (detail.getWinLoss().compareTo(BigDecimal.ZERO) < 0) {
                detail.setPlatformFee(detail.getWinLoss().abs().multiply(rate));
            } else {
                detail.setPlatformFee(BigDecimal.ZERO);
            }
        });

        Map<String, PlatformDTO> restMap = new HashMap<>(); //沒有任何注單的平台
        restMap.putAll(finalPlatformNameMap);

        platformDetailReport.stream().forEach(aff -> {
            aff.setWinLoss(aff.getWinLoss().negate());
            aff.setPlatformName(finalPlatformNameMap.get(aff.getPlatformCode()).getName());
            aff.setTotalAmount(aff.getWinLoss().negate().subtract(aff.getPlatformFee()));
            restMap.remove(aff.getPlatformCode());
        });

        restMap.entrySet().forEach(platform -> {
            platformDetailReport.add(AffiliatePlatformDetailVO.builder().platformCode(platform.getKey())
                    .platformName(platform.getValue().getName())
                    .platformFee(BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO)
                    .winLoss(BigDecimal.ZERO)
                    .build());
        });

        return platformDetailReport;
    }

    @Override
    public List<AffAffiliateCommissionPlatformRecord> getPlatformDetail(String date) {
        Long agentId = AffiliateUtils.getAffiliateId();
        return affAffiliateCommissionPlatformRecordService.lambdaQuery().eq(AffAffiliateCommissionPlatformRecord::getAgentId, agentId)
                .eq(AffAffiliateCommissionPlatformRecord::getIssueDate, date).list();
    }

    @Override
    public IPage<PerformanceReportVO> findPerformanceReport(FindPerformanceReportForm form) {
        if (Objects.nonNull(form.getState()) && form.getState() == 5) {
            return this.lambdaQuery()
                    .eq(StringUtils.isNotBlank(form.getMonth()), AffAffiliateCommissionRecord::getVerifyDate, form.getMonth())
                    .eq(Objects.nonNull(form.getLevel()), AffAffiliateCommissionRecord::getLevel, form.getLevel())
                    .eq(AffAffiliateCommissionRecord::getStatus, AffiliateCommissionStatusEnum.PASS.getCode())
                    .eq(AffAffiliateCommissionRecord::getState, AffiliateCommissionStateEnum.ISSUED.getCode())
                    .eq(StringUtils.isNotBlank(form.getUsername()), AffAffiliateCommissionRecord::getAgentUsername, form.getUsername())
                    .page(new Page<>(form.getPageNum(), form.getPageSize()))
                    .convert(affiliateCommissionRecordConverter::toVO);
        }

        return this.lambdaQuery()
                .eq(StringUtils.isNotBlank(form.getMonth()), AffAffiliateCommissionRecord::getVerifyDate, form.getMonth())
                .eq(Objects.nonNull(form.getLevel()), AffAffiliateCommissionRecord::getLevel, form.getLevel())
                .eq(Objects.nonNull(form.getState()), AffAffiliateCommissionRecord::getStatus, form.getState())
                .eq(Objects.nonNull(form.getState()), AffAffiliateCommissionRecord::getState, AffiliateCommissionStateEnum.NOT_YET.getCode())
                .eq(StringUtils.isNotBlank(form.getUsername()), AffAffiliateCommissionRecord::getAgentUsername, form.getUsername())
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(affiliateCommissionRecordConverter::toVO);
    }

    @Override
    public List<PerformanceReportExportVO> exportPerformanceReport(FindPerformanceReportExportForm form) {
        if (Objects.nonNull(form.getState()) && form.getState() == 5) {
            return this.lambdaQuery()
                    .eq(StringUtils.isNotBlank(form.getMonth()), AffAffiliateCommissionRecord::getVerifyDate, form.getMonth())
                    .eq(Objects.nonNull(form.getLevel()), AffAffiliateCommissionRecord::getLevel, form.getLevel())
                    .eq(AffAffiliateCommissionRecord::getStatus, AffiliateCommissionStatusEnum.PASS.getCode())
                    .eq(AffAffiliateCommissionRecord::getState, AffiliateCommissionStateEnum.ISSUED.getCode())
                    .eq(StringUtils.isNotBlank(form.getUsername()), AffAffiliateCommissionRecord::getAgentUsername, form.getUsername())
                    .list()
                    .stream()
                    .map(affiliateCommissionRecordConverter::toReportVO)
                    .collect(Collectors.toList());
        }

        return this.lambdaQuery()
                .eq(StringUtils.isNotBlank(form.getMonth()), AffAffiliateCommissionRecord::getVerifyDate, form.getMonth())
                .eq(Objects.nonNull(form.getLevel()), AffAffiliateCommissionRecord::getLevel, form.getLevel())
                .eq(Objects.nonNull(form.getState()), AffAffiliateCommissionRecord::getStatus, form.getState())
                .eq(Objects.nonNull(form.getState()), AffAffiliateCommissionRecord::getState, AffiliateCommissionStateEnum.NOT_YET.getCode())
                .eq(StringUtils.isNotBlank(form.getUsername()), AffAffiliateCommissionRecord::getAgentUsername, form.getUsername())
                .list()
                .stream()
                .map(affiliateCommissionRecordConverter::toReportVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verifyCommission(Long id, int status) {
        AffAffiliateCommissionRecord record = this.lambdaQuery().eq(AffAffiliateCommissionRecord::getId, id).oneOpt().orElse(null);

        //如果是活躍人數或未達標準，不更改原始狀態，但要改審核狀態
        return this.lambdaUpdate().set(record.getStatus() != AffiliateCommissionStatusEnum.ACTIVE_MEMBER_NOT_ENOUGH.getCode() && record.getStatus() != AffiliateCommissionStatusEnum.UNDER_ISSUE.getCode()
                        , AffAffiliateCommissionRecord::getStatus, status)
                .set(AffAffiliateCommissionRecord::getVerifyStatus, status)
                .eq(AffAffiliateCommissionRecord::getId, id).update();
    }

//    private BigDecimal getSubWinLoss(Long agentId, String date) {
//        BigDecimal total = BigDecimal.ZERO;
//        LocalDateTime floor = DateUtil.strToFirstDayOfMonthWithLocalDateTime(date);
//        LocalDateTime ceiling = floor.plusMonths(1);
//
//        List<Long> subAgentIds = affiliateService.lambdaQuery().eq(Affiliate::getParentId, agentId).list().stream().map(Affiliate::getId).collect(Collectors.toList());
//
//
//        if (subAgentIds.size() == 0) {
//            return total;
//        } else {
//            List<Long> membersOfSubAgent = affiliateMemberService.getMembersOfSubAgent(agentId).stream().mapToLong(AffiliateMember::getMemberId).boxed().collect(Collectors.toList());
////            for (Long subAgentId : subAgentIds) {
//            if (membersOfSubAgent.size() == 0) return total;
//            AffMemberTransactions affMemberTransactions = affMemberTransactionsService.getBaseMapper().selectOne(new QueryWrapper<AffMemberTransactions>()
//                    .select("sum(win_loss) as winLoss ")
//                    .lambda()
//                    .in(AffMemberTransactions::getParentId, membersOfSubAgent)
//                    .ge(AffMemberTransactions::getGmtCreate, floor)
//                    .lt(AffMemberTransactions::getGmtCreate, ceiling));
//            total = affMemberTransactions==null ? BigDecimal.ZERO : affMemberTransactions.getWinLoss().negate();
//        }
//        return total;
//    }

    public int deleteCommissionRecord(String date, int level) {
        return this.baseMapper.delete(new LambdaQueryWrapper<AffAffiliateCommissionRecord>()
                .eq(AffAffiliateCommissionRecord::getVerifyDate, date)
                .eq(AffAffiliateCommissionRecord::getLevel, level));

    }

    @Override
    @Transactional
    public Boolean issueCommission(String date) {
        List<AffAffiliateCommissionRecord> list = this.lambdaQuery().eq(AffAffiliateCommissionRecord::getVerifyDate, date).list();
        List<Affiliate> affList = new ArrayList<>();
        list.forEach(aff -> {
            Affiliate affiliate = affiliateService.lambdaQuery()
                    .eq(Affiliate::getId, aff.getAgentId())
                    .one();
            //如果能發佣金，表示前期缺額已填滿，所以歸零
            if (aff.getTotalCommission().compareTo(BigDecimal.ZERO) > 0) {
                affiliate.setDifference(BigDecimal.ZERO);
            } else {
                affiliate.setDifference(affiliate.getDifference().add(aff.getNetProfit()).setScale(2, RoundingMode.DOWN));
            }
//            affiliate.setDifference(aff.getTotalCommission().compareTo(BigDecimal.ZERO)>0 ? affiliate.getDifference().add(aff.getDifference()));
            affList.add(affiliate);
            if (aff.getTotalCommission().compareTo(BigDecimal.ZERO) > 0 && aff.getVerifyStatus() == AffiliateCommissionStatusEnum.PASS.getCode()) {
                kafkaTemplate.send(TopicConstants.AFFILIATE_BALANCE_CHANGE, AddAffiliateBalanceDTO.builder()
                        .affiliateId(aff.getAgentId())
                        .serialNo(UUIDUtils.genOrderId("AC"))
                        .type(AffiliateBalanceChangeTypeEnum.BACK_COMMISSION.getValue())
                        .amount(aff.getTotalCommission())
                        .gmtCreate(LocalDateTime.now())
                        .build());
            }
        });

        //更新缺額
        affiliateService.updateBatchById(affList);

        //更新佣金總表
        affAffiliateCommissionTotalRecordMapper.update(null, new UpdateWrapper<AffAffiliateCommissionTotalRecord>()
                .eq("verify_date", date).set("status", AffiliateCommissionTotalStateEnum.ISSUED.getCode()).set("issue_date", LocalDateTime.now()));

        //更新每一筆記錄為已發送
        this.lambdaUpdate().eq(AffAffiliateCommissionRecord::getVerifyDate, date)
                .set(AffAffiliateCommissionRecord::getState, AffiliateCommissionStateEnum.ISSUED.getCode())
                .set(AffAffiliateCommissionRecord::getIssueDate, LocalDateTime.now()).update();

        return true;
    }

    public List<Long> findTeamAffiliate(Long agentId, Map<String, Boolean> isLevelOne) {
        Affiliate self = affiliateService.lambdaQuery().eq(Affiliate::getId, agentId).one();
        Optional.ofNullable(isLevelOne).ifPresent(x -> x.put("isLevelOne", self.getLevel().equals(1)));
        List<Affiliate> sub = affiliateService.lambdaQuery().eq(Affiliate::getParentId, agentId).list();
        //sub.add(self);
        return sub.stream().filter(x -> x.getEnable().equals(1)).map(Affiliate::getId).collect(Collectors.toList());
    }
}




