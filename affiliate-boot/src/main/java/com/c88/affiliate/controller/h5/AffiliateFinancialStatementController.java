package com.c88.affiliate.controller.h5;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.FindAffiliateFinancialStatementPersonalForm;
import com.c88.affiliate.pojo.form.FindAffiliateFinancialStatementTeamForm;
import com.c88.affiliate.pojo.vo.AffiliateFinancialStatementPersonalBonusVO;
import com.c88.affiliate.pojo.vo.AffiliateFinancialStatementPersonalPlatformVO;
import com.c88.affiliate.pojo.vo.AffiliateFinancialStatementPersonalRechargeVO;
import com.c88.affiliate.pojo.vo.AffiliateFinancialStatementPersonalTotalWinLossVO;
import com.c88.affiliate.pojo.vo.AffiliateFinancialStatementPersonalVO;
import com.c88.affiliate.pojo.vo.FindAffiliateFinancialStatementTeamVO;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.enums.BalanceChangeTypeLinkEnum;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import com.c88.game.adapter.api.PlatformClient;
import com.c88.game.adapter.dto.PlatformDTO;
import com.c88.member.vo.OptionVO;
import com.c88.payment.client.MemberRechargeTypeClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "代理財務報表")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/affiliate/financial/statement")
public class AffiliateFinancialStatementController {

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final IAffiliateService iAffiliateService;

    private final PlatformClient platformClient;

    private final MemberRechargeTypeClient memberRechargeTypeClient;

    @Transactional
    @Operation(summary = "個人財務")
    @GetMapping("/personal")
    public Result<AffiliateFinancialStatementPersonalVO> findAffiliateFinancialStatementPersonal(@ParameterObject FindAffiliateFinancialStatementPersonalForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        String username = AffiliateUtils.getUsername();
        if (Objects.isNull(affiliateId) || StringUtils.isBlank(username)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        List<Long> memberIds = iAffiliateMemberService.lambdaQuery()
                .select(AffiliateMember::getMemberId)
                .eq(AffiliateMember::getParentId, affiliateId)
                .list()
                .stream()
                .map(AffiliateMember::getMemberId)
                .collect(Collectors.toList());

        AffiliateFinancialStatementPersonalVO affiliateFinancialStatementPersonal = AffiliateFinancialStatementPersonalVO.builder().build();
        if (CollectionUtils.isEmpty(memberIds)) {
            return Result.success(affiliateFinancialStatementPersonal);
        }

        // 取得場館資訊
        Map<String, BigDecimal> platformRateMap = Map.of();
        Result<List<PlatformDTO>> platformResult = platformClient.findAllPlatformDTO();
        if (Result.isSuccess(platformResult)) {
            platformRateMap = platformResult.getData()
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(PlatformDTO::getCode, PlatformDTO::getRate));
        }

        // 取得會員交易資訊
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startTime = LocalDateTime.of(LocalDate.parse(form.getStartTime(), dtf), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(form.getEndTime(), dtf), LocalTime.MAX);

        List<AffMemberTransactions> memberTransactions = iAffMemberTransactionsService.lambdaQuery()
                .ge(BaseEntity::getGmtCreate, startTime)
                .le(BaseEntity::getGmtCreate, endTime)
                .in(AffMemberTransactions::getMemberId, memberIds)
                .list();

        // 取得存款,存款明細,存送優惠

        List<OptionVO<Integer>> rechargeTypeOption = new ArrayList<>();
        Result<List<OptionVO<Integer>>> rechargeTypeOptionResult = memberRechargeTypeClient.findRechargeTypeOption();
        if (Result.isSuccess(rechargeTypeOptionResult)) {
            rechargeTypeOption = rechargeTypeOptionResult.getData();
        }

        List<AffMemberTransactions> rechargeTransactions = memberTransactions.stream()
                .filter(filter -> Objects.equals(filter.getType(), AffMemberTransactionsTypeEnum.RECHARGE.getCode()))
                .collect(Collectors.toList());

        List<AffiliateFinancialStatementPersonalRechargeVO> recharges = rechargeTypeOption.stream()
                .map(rechargeType ->
                        AffiliateFinancialStatementPersonalRechargeVO.builder()
                                .rechargeTypeName(rechargeType.getLabel())
                                .amount(
                                        rechargeTransactions.stream()
                                                .filter(filter -> Objects.equals(filter.getI18n(), rechargeType.getLabel()))
                                                .map(AffMemberTransactions::getRechargeAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                )
                                .build()
                )
                .collect(Collectors.toList());

        affiliateFinancialStatementPersonal.setRechargeDetail(recharges);
        affiliateFinancialStatementPersonal.setRecharge(recharges.stream().map(AffiliateFinancialStatementPersonalRechargeVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        affiliateFinancialStatementPersonal.setRechargeAward(rechargeTransactions.stream().map(AffMemberTransactions::getRechargeAwardAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // 取得提款
        List<AffMemberTransactions> withdrawTransactions = memberTransactions.stream()
                .filter(filter -> Objects.equals(filter.getType(), AffMemberTransactionsTypeEnum.WITHDRAW.getCode()))
                .collect(Collectors.toList());

        affiliateFinancialStatementPersonal.setWithdraw(withdrawTransactions.stream().map(AffMemberTransactions::getWithdrawAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // 取得紅利 紅利詳情
        List<AffMemberTransactions> bonusTransactions = memberTransactions.stream()
                .filter(filter -> Objects.equals(filter.getType(), AffMemberTransactionsTypeEnum.BONUS.getCode()))
                .collect(Collectors.toList());

        // 取得紅利項目
        List<BalanceChangeTypeLinkEnum> bonusTypeEnums = BalanceChangeTypeLinkEnum.getEnumsByType(BalanceChangeTypeLinkEnum.BONUS.getCode());

        List<AffiliateFinancialStatementPersonalBonusVO> bonus = bonusTypeEnums.stream()
                .map(bonusTypeEnum ->
                        AffiliateFinancialStatementPersonalBonusVO.builder()
                                .bonusType(bonusTypeEnum.getI18n())
                                .amount(
                                        bonusTransactions.stream()
                                                .filter(filter -> Objects.equals(filter.getI18n(), bonusTypeEnum.getI18n()))
                                                .map(AffMemberTransactions::getAwardAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                )
                                .build()
                )
                .collect(Collectors.toList());

        affiliateFinancialStatementPersonal.setBonusDetail(bonus);
        affiliateFinancialStatementPersonal.setBonus(bonus.stream().map(AffiliateFinancialStatementPersonalBonusVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // 取得場館費 總輸贏
        List<AffMemberTransactions> betTransactions = memberTransactions.stream()
                .filter(filter -> Objects.equals(filter.getType(), AffMemberTransactionsTypeEnum.SETTLE.getCode()))
                .collect(Collectors.toList());

        // 總輸贏
        Map<String, BigDecimal> finalPlatformRateMap = platformRateMap;
        Map<String, List<AffiliateFinancialStatementPersonalTotalWinLossVO>> winLossByPlatformName = betTransactions.stream()
                .map(bet ->
                        AffiliateFinancialStatementPersonalTotalWinLossVO.builder()
                                .platformName(bet.getPlatformCode())
                                .gameType(bet.getI18n())
                                .winLoss(bet.getWinLoss().negate())
                                .platformRate(finalPlatformRateMap.getOrDefault(bet.getPlatformCode(), BigDecimal.ZERO).multiply(new BigDecimal("100")))
                                .platformFee(bet.getWinLoss().negate().multiply(finalPlatformRateMap.getOrDefault(bet.getPlatformCode(), BigDecimal.ZERO)))
                                .bet(bet.getValidBetAmount())
                                .build()
                )
                .collect(Collectors.groupingBy(AffiliateFinancialStatementPersonalTotalWinLossVO::getPlatformName));

        List<AffiliateFinancialStatementPersonalTotalWinLossVO> winLossDetails = winLossByPlatformName.values()
                .stream()
                .map(byPlatform -> {
                            Map<String, List<AffiliateFinancialStatementPersonalTotalWinLossVO>> byGameType = byPlatform.stream()
                                    .collect(Collectors.groupingBy(AffiliateFinancialStatementPersonalTotalWinLossVO::getGameType));

                            return byGameType.values()
                                    .stream()
                                    .map(platformToGameType -> {
                                                AffiliateFinancialStatementPersonalTotalWinLossVO vo = platformToGameType.stream()
                                                        .findFirst()
                                                        .orElse(AffiliateFinancialStatementPersonalTotalWinLossVO.builder().build());

                                                // 總輸贏
                                                BigDecimal winLoss = platformToGameType.stream()
                                                        .map(AffiliateFinancialStatementPersonalTotalWinLossVO::getWinLoss)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                                // 場館費
                                                BigDecimal platformFee = platformToGameType.stream()
                                                        .map(AffiliateFinancialStatementPersonalTotalWinLossVO::getPlatformFee)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                                // 流水
                                                BigDecimal bet = platformToGameType.stream()
                                                        .map(AffiliateFinancialStatementPersonalTotalWinLossVO::getBet)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                                return AffiliateFinancialStatementPersonalTotalWinLossVO.builder()
                                                        .platformName(vo.getPlatformName())
                                                        .gameType(vo.getGameType())
                                                        .winLoss(winLoss)
                                                        .platformFee(platformFee.compareTo(BigDecimal.ZERO) > 0 ? platformFee : BigDecimal.ZERO)
                                                        .platformRate(vo.getPlatformRate())
                                                        .bet(bet)
                                                        .build();
                                            }
                                    )
                                    .collect(Collectors.toList());
                        }
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // 場館費及場館費詳情
        List<AffiliateFinancialStatementPersonalPlatformVO> platformDetails = winLossDetails.stream()
                .map(vo ->
                        AffiliateFinancialStatementPersonalPlatformVO.builder()
                                .platformName(vo.getPlatformName())
                                .gameType(vo.getGameType())
                                .winLoss(vo.getWinLoss())
                                .platformRate(vo.getPlatformRate())
                                .platformFee(vo.getPlatformFee())
                                .build()
                )
                .collect(Collectors.toList());
        affiliateFinancialStatementPersonal.setPlatformDetail(platformDetails);
        affiliateFinancialStatementPersonal.setPlatform(platformDetails.stream().map(AffiliateFinancialStatementPersonalPlatformVO::getPlatformFee).reduce(BigDecimal.ZERO, BigDecimal::add));

        affiliateFinancialStatementPersonal.setTotalWinLossDetail(winLossDetails);
        affiliateFinancialStatementPersonal.setTotalWinLoss(platformDetails.stream().map(AffiliateFinancialStatementPersonalPlatformVO::getWinLoss).reduce(BigDecimal.ZERO, BigDecimal::add));
        affiliateFinancialStatementPersonal.setBet(betTransactions.stream().map(AffMemberTransactions::getValidBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // todo 取得帳戶調整
        affiliateFinancialStatementPersonal.setAccountAdjustment(BigDecimal.ZERO);

        // 淨輸贏 (淨輸贏=總输赢-會員紅利-會員反水-場館費-微調扣除金額)
        affiliateFinancialStatementPersonal.setNetWinLoss(
                Stream.of(
                                affiliateFinancialStatementPersonal.getBonus(),
                                affiliateFinancialStatementPersonal.getPlatform(),
                                affiliateFinancialStatementPersonal.getRechargeAward(),
                                affiliateFinancialStatementPersonal.getAccountAdjustment()
                        )
                        .reduce(affiliateFinancialStatementPersonal.getTotalWinLoss(), BigDecimal::subtract)
        );

        return Result.success(affiliateFinancialStatementPersonal);
    }

    @Operation(summary = "團隊財務")
    @GetMapping("/team")
    public PageResult<FindAffiliateFinancialStatementTeamVO> findAffiliateFinancialStatementTeam(@Validated @ParameterObject FindAffiliateFinancialStatementTeamForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        String[] yearMonth = form.getMonth().split("-");

        LocalDateTime startTime = LocalDateTime.of(LocalDate.of(Integer.parseInt(yearMonth[0]), Integer.parseInt(yearMonth[1]), 1), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(startTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate(), LocalTime.MAX);

        List<Affiliate> affiliates = iAffiliateService.lambdaQuery()
                .eq(Affiliate::getId, affiliateId)
                .or()
                .eq(Affiliate::getParentId, affiliateId)
                .and(StringUtils.isNotBlank(form.getUsername()), wrapper -> wrapper.eq(Affiliate::getUsername, form.getUsername()))
                .list();

        List<AffMemberTransactions> transactions = iAffMemberTransactionsService.lambdaQuery()
                .between(BaseEntity::getGmtCreate, startTime, endTime)
                .eq(StringUtils.isNotBlank(form.getUsername()), AffMemberTransactions::getParentUsername, form.getUsername())
                .and(wrapper -> wrapper.likeRight(AffMemberTransactions::getParents, affiliateId).or().eq(AffMemberTransactions::getParentId, affiliateId))
                .list();

        Result<List<PlatformDTO>> platformsResult = platformClient.findAllPlatformDTO();
        List<PlatformDTO> platforms = Collections.emptyList();
        if (Result.isSuccess(platformsResult)) {
            platforms = platformsResult.getData();
        }

        List<PlatformDTO> finalPlatforms = platforms;
        Supplier<Stream<FindAffiliateFinancialStatementTeamVO>> vosStreamSupplier = () -> affiliates.stream()
                .map(affiliate -> {
                            List<AffMemberTransactions> transactionsByParentId = transactions.stream()
                                    .filter(filter -> Objects.equals(filter.getParentUsername().toUpperCase(), affiliate.getUsername().toUpperCase()))
                                    .collect(Collectors.toList());

                            // 充值
                            BigDecimal recharge = transactionsByParentId.stream()
                                    .map(AffMemberTransactions::getRechargeAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            // 提款
                            BigDecimal withdraw = transactionsByParentId.stream()
                                    .map(AffMemberTransactions::getWithdrawAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            // 紅利
                            BigDecimal bonus = transactionsByParentId.stream()
                                    .map(AffMemberTransactions::getAwardAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            // 存送優惠
                            BigDecimal rechargeAward = transactionsByParentId.stream()
                                    .map(AffMemberTransactions::getRechargeAwardAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            // 場館費
                            Map<String, List<AffMemberTransactions>> byPlatformCode = transactionsByParentId.stream()
                                    .filter(filter -> Objects.equals(filter.getType(), AffMemberTransactionsTypeEnum.SETTLE.getCode()))
                                    .collect(Collectors.groupingBy(AffMemberTransactions::getPlatformCode));

                            Map<String, BigDecimal> platformRateMap = finalPlatforms.stream()
                                    .collect(Collectors.toMap(PlatformDTO::getCode, PlatformDTO::getRate));

                            BigDecimal platformFee = byPlatformCode.values()
                                    .stream()
                                    .map(transactionsBySettles ->
                                            transactionsBySettles.stream()
                                                    .map(x -> platformRateMap.getOrDefault(x.getPlatformCode(), BigDecimal.ZERO).multiply(x.getWinLoss().negate()))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    )
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            platformFee = platformFee.compareTo(BigDecimal.ZERO) > 0 ? platformFee : BigDecimal.ZERO;

                            // 總輸贏
                            BigDecimal winLoss = transactionsByParentId.stream()
                                    .map(AffMemberTransactions::getWinLoss)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            return FindAffiliateFinancialStatementTeamVO.builder()
                                    .username(affiliate.getUsername())
                                    .recharge(recharge)
                                    .withdraw(withdraw)
                                    .bonus(bonus)
                                    .rechargeAward(rechargeAward)
                                    .platform(platformFee)
                                    .totalWinLoss(winLoss.negate())
                                    .netWinLoss(Stream.of(bonus, platformFee, rechargeAward).reduce(winLoss.negate(), BigDecimal::subtract))
                                    .isCaptain(Objects.equals(affiliate.getMasterId(), affiliate.getId()) ? 1 : 0)
                                    .build();
                        }
                );

        // 排序
        if (StringUtils.isNotBlank(form.getColumn())) {
            if (form.getOrder() == 0) {
                switch (form.getColumn()) {
                    case "rechargeAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getRecharge));
                        break;
                    case "withdrawAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getWithdraw));
                        break;
                    case "totalWinLoss":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getTotalWinLoss));
                        break;
                    case "platformFee":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getPlatform));
                        break;
                    case "bonusAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getBonus));
                        break;
                    case "rechargeAwardAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getRechargeAward));
                        break;
                    case "netWinLoss":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getNetWinLoss));
                        break;
                    default:
                }
            } else {
                switch (form.getColumn()) {
                    case "rechargeAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getRecharge).reversed());
                        break;
                    case "withdrawAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getWithdraw).reversed());
                        break;
                    case "totalWinLoss":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getTotalWinLoss).reversed());
                        break;
                    case "platformFee":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getPlatform).reversed());
                        break;
                    case "bonusAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getBonus).reversed());
                        break;
                    case "rechargeAwardAmount":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getRechargeAward).reversed());
                        break;
                    case "netWinLoss":
                        vosStreamSupplier.get().sorted(Comparator.comparing(FindAffiliateFinancialStatementTeamVO::getNetWinLoss).reversed());
                        break;
                    default:
                }
            }
        }

        List<FindAffiliateFinancialStatementTeamVO> vos = vosStreamSupplier.get().collect(Collectors.toList());

        // 放入page
        Page<FindAffiliateFinancialStatementTeamVO> voPage = new Page<>(form.getPageNum(), form.getPageSize());
        voPage.setRecords(
                vos.stream()
                        .skip((long) (form.getPageNum() - 1) * form.getPageSize())
                        .limit(form.getPageSize())
                        .collect(Collectors.toList())
        );
        voPage.setTotal(vos.size());
        voPage.setPages(vos.size() / form.getPageSize());

        return PageResult.success(voPage);
    }

}
