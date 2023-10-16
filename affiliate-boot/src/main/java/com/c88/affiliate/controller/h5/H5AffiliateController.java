package com.c88.affiliate.controller.h5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.mapper.AffMemberTransactionsMapper;
import com.c88.affiliate.mapper.AffiliateMemberMapper;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.entity.AffiliateProtectPasswordQuestion;
import com.c88.affiliate.pojo.entity.Label;
import com.c88.affiliate.pojo.entity.MemberLabel;
import com.c88.affiliate.pojo.form.FindAffiliateMemberInfoForm;
import com.c88.affiliate.pojo.form.H5ChangePasswordForm;
import com.c88.affiliate.pojo.form.ModifyAffiliateForgotPasswordForm;
import com.c88.affiliate.pojo.form.ModifyAffiliateMemberInfoNoteForm;
import com.c88.affiliate.pojo.form.ModifyImForm;
import com.c88.affiliate.pojo.form.RegisterAffiliateForm;
import com.c88.affiliate.pojo.form.SearchAffiliateMemberForm;
import com.c88.affiliate.pojo.form.SearchAffiliateTeamMemberForm;
import com.c88.affiliate.pojo.form.SearchMemberDepositRecordForm;
import com.c88.affiliate.pojo.form.SearchMemberPlayingRecordForm;
import com.c88.affiliate.pojo.form.VerifyAffiliateForgotPasswordForm;
import com.c88.affiliate.pojo.form.VerifyEmailForm;
import com.c88.affiliate.pojo.form.VerifyMobileForm;
import com.c88.affiliate.pojo.vo.AffiliateForgotPasswordRecoverWayVO;
import com.c88.affiliate.pojo.vo.AffiliateMemberInfoVO;
import com.c88.affiliate.pojo.vo.AffiliateMemberPlatformDetailVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import com.c88.affiliate.pojo.vo.H5AffiliateTeamMemberVO;
import com.c88.affiliate.pojo.vo.H5AffiliateVO;
import com.c88.affiliate.pojo.vo.H5MemberPlayingRecordVO;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateProtectPasswordQuestionService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.affiliate.service.ILabelService;
import com.c88.affiliate.service.IMemberLabelService;
import com.c88.affiliate.service.IMemberPlayRecordService;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.redis.annotation.Limit;
import com.c88.common.redis.enums.LimitType;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import com.c88.game.adapter.api.PlatformClient;
import com.c88.game.adapter.dto.PlatformDTO;
import com.c88.member.api.MemberFeignClient;
import com.c88.member.dto.MemberInfoDTO;
import com.c88.payment.client.MemberBalanceClient;
import com.c88.payment.dto.MemberBalanceInfoDTO;
import com.c88.payment.vo.MemberDepositDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "『前台』- 代理相關")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/affiliate")
public class H5AffiliateController {

    private final IAffiliateService iAffiliateService;

    private final MemberFeignClient memberFeignClient;

    private final ILabelService iLabelService;

    private final IMemberLabelService iMemberLabelService;

    private final AffMemberTransactionsMapper affMemberTransactionsMapper;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final AffiliateMemberMapper affiliateMemberMapper;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final PlatformClient platformClient;

    private final MemberBalanceClient memberBalanceClient;

    private final IAffiliateProtectPasswordQuestionService iAffiliateProtectPasswordQuestionService;

    private final IMemberPlayRecordService iMemberPlayRecordService;

    @Operation(summary = "代理註冊")
    @PostMapping("/register")
    public Result<Boolean> register(@Validated @RequestBody RegisterAffiliateForm form) {
        iAffiliateService.register(form);
        return Result.success(true);
    }

    @Operation(summary = "會員管理-會員管理")
    @GetMapping("/member")
    public PageResult<H5AffiliateMemberVO> findAffiliateMember(@Validated @ParameterObject SearchAffiliateMemberForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        List<H5AffiliateMemberVO> affiliateMembers = affiliateMemberMapper.findAffiliateMembers(form, affiliateId);

        if (affiliateMembers.isEmpty()) {
            return PageResult.success(new Page<>(form.getPageNum(), form.getPageSize()));
        }

        // 會員帳號
        List<String> usernames = affiliateMembers.stream()
                .map(H5AffiliateMemberVO::getUsername)
                .collect(Collectors.toList());

        // 取得會員交易訊息
        List<Map<String, Object>> memberTransactionsMaps = affMemberTransactionsMapper.selectMaps(Wrappers.<AffMemberTransactions>query()
                .select(
                        "member_id as memberId",
                        "member_username as username",
                        "sum(recharge_amount) as recharge",
                        "sum(withdraw_amount) as withdrawal",
                        "sum(win_loss) as winLoss"
                )
                .lambda()
                .in(AffMemberTransactions::getType,
                        AffMemberTransactionsTypeEnum.RECHARGE.getCode(),
                        AffMemberTransactionsTypeEnum.WITHDRAW.getCode(),
                        AffMemberTransactionsTypeEnum.SETTLE.getCode(),
                        AffMemberTransactionsTypeEnum.BONUS.getCode()
                )
                .in(AffMemberTransactions::getMemberUsername, usernames)
                .ge(BaseEntity::getGmtCreate, LocalDateTime.of(form.getStartTime(), LocalTime.MIN))
                .le(BaseEntity::getGmtCreate, LocalDateTime.of(form.getEndTime(), LocalTime.MAX))
                .groupBy(AffMemberTransactions::getMemberUsername)
                .having(Objects.nonNull(form.getMinRecharge()), "recharge >= {0}", form.getMinRecharge())
                .having(Objects.nonNull(form.getMaxRecharge()), "recharge <= {0}", form.getMaxRecharge())
        );

        // 取得會員資訊
        Result<List<MemberInfoDTO>> memberInfoByUsernamesResult = memberFeignClient.getMemberInfoByUsernames(usernames);
        if (Result.isSuccess(memberInfoByUsernamesResult)) {
            List<MemberInfoDTO> memberInfos = memberInfoByUsernamesResult.getData();

            affiliateMembers.forEach(affiliateMember -> {
                        // 取得會員交易訊息
                        Map<String, Object> memberTransactionsMap = memberTransactionsMaps.stream().filter(filter -> affiliateMember.getUsername().equals(filter.get("username")))
                                .findFirst()
                                .orElse(new HashMap<>());

                        // 取得會員基本訊息
                        MemberInfoDTO memberInfoDTO = memberInfos.stream()
                                .filter(filter -> filter.getUsername().equals(affiliateMember.getUsername()))
                                .findFirst()
                                .orElse(MemberInfoDTO.builder().build());

                        affiliateMember.setRecharge((BigDecimal) memberTransactionsMap.getOrDefault("recharge", BigDecimal.ZERO));
                        affiliateMember.setWithdrawal((BigDecimal) memberTransactionsMap.getOrDefault("withdrawal", BigDecimal.ZERO));
                        affiliateMember.setWinLoss(((BigDecimal) memberTransactionsMap.getOrDefault("winLoss", BigDecimal.ZERO)).negate());
                        affiliateMember.setVipName(memberInfoDTO.getCurrentVipName());
                        affiliateMember.setRegisterTime(memberInfoDTO.getRegisterTime());
                        affiliateMember.setRealName(memberInfoDTO.getRealName());
                        affiliateMember.setLastLoginTime(memberInfoDTO.getLastLoginTime());
                    }
            );
        }

        // 有輸入最低金額或最高時過濾會員
        if (Objects.nonNull(form.getMinRecharge())) {
            affiliateMembers = affiliateMembers.stream()
                    .filter(filter -> filter.getRecharge().compareTo(form.getMinRecharge()) >= 0)
                    .collect(Collectors.toList());
        }
        if (Objects.nonNull(form.getMaxRecharge())) {
            affiliateMembers = affiliateMembers.stream()
                    .filter(filter -> filter.getRecharge().compareTo(form.getMaxRecharge()) <= 0)
                    .collect(Collectors.toList());
        }

        // 放入page
        Page<H5AffiliateMemberVO> affiliateMembersPage = new Page<>(form.getPageNum(), form.getPageSize());
        affiliateMembersPage.setRecords(
                affiliateMembers.stream()
                        .skip((long) (form.getPageNum() - 1) * form.getPageSize())
                        .limit(form.getPageSize())
                        .collect(Collectors.toList())
        );
        affiliateMembersPage.setTotal(affiliateMembers.size());
        affiliateMembersPage.setPages(affiliateMembers.size() / form.getPageSize());

        return PageResult.success(affiliateMembersPage);
    }

    @Operation(summary = "會員管理-團隊會員-會員詳情-編輯會員備註")
    @PutMapping("/member/info/note")
    public Result<Boolean> modifyAffiliateMemberInfoNote(@RequestBody ModifyAffiliateMemberInfoNoteForm form) {
        return Result.success(iAffiliateMemberService.lambdaUpdate()
                .eq(AffiliateMember::getMemberUsername, form.getUsername())
                .set(AffiliateMember::getNote, form.getNote())
                .update());
    }

    @Operation(summary = "會員管理-團隊會員-會員詳情")
    @GetMapping("/member/info")
    public Result<AffiliateMemberInfoVO> findAffiliateMemberInfo(@ParameterObject FindAffiliateMemberInfoForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        AffiliateMemberInfoVO affiliateMemberInfo = affMemberTransactionsMapper.findAffiliateMemberInfo(form.getUsername(), LocalDateTime.of(form.getStartTime(), LocalTime.MIN), LocalDateTime.of(form.getEndTime(), LocalTime.MAX));

        // 取得平台
        Result<List<PlatformDTO>> platformResult = platformClient.findAllPlatformDTO();
        Map<String, String> platformNameMap = new HashMap<>();
        if (Result.isSuccess(platformResult)) {
            platformNameMap = platformResult.getData()
                    .stream()
                    .collect(Collectors.toMap(PlatformDTO::getCode, PlatformDTO::getName));
        }

        // 取得首次充值時間
        Result<List<MemberBalanceInfoDTO>> memberBalanceInfoByUsernamesResult = memberBalanceClient.findMemberBalanceInfoByUsernames(List.of(affiliateMemberInfo.getUsername()));
        if (Result.isSuccess(memberBalanceInfoByUsernamesResult)) {
            affiliateMemberInfo.setFirstRechargeTime(
                    memberBalanceInfoByUsernamesResult.getData()
                            .stream()
                            .findFirst()
                            .map(MemberBalanceInfoDTO::getFirstRechargeTime)
                            .orElse(null)
            );
        }

        // 取得會員標籤
        Map<Long, String> labelMap = iLabelService.list()
                .stream()
                .collect(Collectors.toMap(Label::getId, Label::getName));

        affiliateMemberInfo.setMemberLabelNames(
                iMemberLabelService.lambdaQuery()
                        .eq(MemberLabel::getMemberId, affiliateMemberInfo.getMemberId())
                        .list()
                        .stream()
                        .map(x -> labelMap.get(x.getLabelId()))
                        .collect(Collectors.toList())
        );

        // 取得會員平台遊玩紀錄
        Map<String, String> finalPlatformNameMap = platformNameMap;
        affiliateMemberInfo.setPlatformInfos(affMemberTransactionsMapper.selectMaps(Wrappers.<AffMemberTransactions>query()
                                .select("platform_code as platformCode",
                                        "game_category_code as gameCategoryCode",
                                        "sum(bet_amount) as bet",
                                        "sum(win_loss) * -1 as winLoss"
                                )
                                .lambda()
                                .eq(AffMemberTransactions::getMemberUsername, form.getUsername())
                                .between(BaseEntity::getGmtCreate, LocalDateTime.of(form.getStartTime(), LocalTime.MIN), LocalDateTime.of(form.getEndTime(), LocalTime.MAX))
                                .groupBy(AffMemberTransactions::getPlatformCode, AffMemberTransactions::getGameCategoryCode)
                        )
                        .stream()
                        .filter(filter -> ((BigDecimal) filter.getOrDefault("bet", BigDecimal.ZERO)).compareTo(BigDecimal.ZERO) != 0 && ((BigDecimal) filter.getOrDefault("winLoss", BigDecimal.ZERO)).compareTo(BigDecimal.ZERO) != 0)
                        .map(platformDetailMap ->
                                AffiliateMemberPlatformDetailVO.builder()
                                        .platformName(finalPlatformNameMap.get((String) platformDetailMap.get("platformCode")))
                                        .gameCategory((String) platformDetailMap.get("gameCategoryCode"))
                                        .bet((BigDecimal) platformDetailMap.getOrDefault("bet", BigDecimal.ZERO))
                                        .winLoss((BigDecimal) platformDetailMap.getOrDefault("winLoss", BigDecimal.ZERO))
                                        .build()
                        )
                        .collect(Collectors.toList())
        );

        // 檢查會員活躍狀態 (活躍玩家為當月充值100或有效流水達到300的用戶)
        affiliateMemberInfo.setActiveState(
                affiliateMemberInfo.getRecharge().compareTo(new BigDecimal("100")) >= 0 ||
                        affiliateMemberInfo.getValidBet().compareTo(new BigDecimal("300")) >= 0 ?
                        1 :
                        0
        );

        return Result.success(affiliateMemberInfo);
    }

    @Operation(summary = "會員管理-會員詳情")
    @GetMapping("/team/member")
    public PageResult<H5AffiliateTeamMemberVO> getTeamMember(@ParameterObject SearchAffiliateTeamMemberForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        List<H5AffiliateTeamMemberVO> affiliateTeamMembers = affiliateMemberMapper.findAffiliateTeamMembersTransaction(form, affiliateId);

        if (affiliateTeamMembers.isEmpty()) {
            return PageResult.success(new Page<>(form.getPageNum(), form.getPageSize()));
        }

        List<Map<String, Object>> memberTransactionMaps = affMemberTransactionsMapper.selectMaps(Wrappers.<AffMemberTransactions>query()
                .select(
                        "member_id as memberId",
                        "member_username as username",
                        "sum(recharge_amount) as recharge",
                        "sum(withdraw_amount) as withdrawal",
                        "sum(win_loss) * -1 as winLoss"
                )
                .lambda()
                .ge(BaseEntity::getGmtCreate, LocalDateTime.of(form.getStartTime(), LocalTime.MIN))
                .le(BaseEntity::getGmtCreate, LocalDateTime.of(form.getEndTime(), LocalTime.MAX))
                .groupBy(AffMemberTransactions::getMemberUsername)
                .having(Objects.nonNull(form.getMinRecharge()), "recharge >= {0}", form.getMinRecharge())
                .having(Objects.nonNull(form.getMaxRecharge()), "recharge <= {0}", form.getMaxRecharge())
        );

        // 會員帳號
        List<String> usernames = affiliateTeamMembers.stream()
                .map(H5AffiliateTeamMemberVO::getUsername)
                .collect(Collectors.toList());

        // 取得會員資訊
        Result<List<MemberInfoDTO>> memberInfoByUsernamesResult = memberFeignClient.getMemberInfoByUsernames(usernames);
        if (Result.isSuccess(memberInfoByUsernamesResult)) {
            List<MemberInfoDTO> memberInfos = memberInfoByUsernamesResult.getData();

            affiliateTeamMembers
                    .forEach(affiliateMember -> {
                                // 取得會員交易訊息
                                Map<String, Object> memberTransactionsMap = memberTransactionMaps.stream()
                                        .filter(filter -> affiliateMember.getUsername().equals(filter.get("username")))
                                        .findFirst()
                                        .orElse(new HashMap<>());

                                MemberInfoDTO memberInfoDTO = memberInfos.stream()
                                        .filter(filter -> filter.getUsername().equals(affiliateMember.getUsername()))
                                        .findFirst()
                                        .orElse(MemberInfoDTO.builder().build());

                                affiliateMember.setRecharge((BigDecimal) memberTransactionsMap.getOrDefault("recharge", BigDecimal.ZERO));
                                affiliateMember.setWithdrawal((BigDecimal) memberTransactionsMap.getOrDefault("withdrawal", BigDecimal.ZERO));
                                affiliateMember.setWinLoss((BigDecimal) memberTransactionsMap.getOrDefault("winLoss", BigDecimal.ZERO));
                                affiliateMember.setRealName(memberInfoDTO.getRealName());
                                affiliateMember.setVipName(memberInfoDTO.getCurrentVipName());
                                affiliateMember.setRegisterTime(memberInfoDTO.getRegisterTime());
                                affiliateMember.setLastLoginTime(memberInfoDTO.getLastLoginTime());
                            }
                    );
        }

        // 有輸入最低金額或最高時過濾會員
        if (Objects.nonNull(form.getMinRecharge())) {
            affiliateTeamMembers = affiliateTeamMembers.stream()
                    .filter(filter -> filter.getRecharge().compareTo(form.getMinRecharge()) >= 0)
                    .collect(Collectors.toList());
        }
        if (Objects.nonNull(form.getMaxRecharge())) {
            affiliateTeamMembers = affiliateTeamMembers.stream()
                    .filter(filter -> filter.getRecharge().compareTo(form.getMaxRecharge()) <= 0)
                    .collect(Collectors.toList());
        }

        // 放入page
        Page<H5AffiliateTeamMemberVO> affiliateMembersPage = new Page<>(form.getPageNum(), form.getPageSize());
        affiliateMembersPage.setRecords(
                affiliateTeamMembers.stream()
                        .skip((long) (form.getPageNum() - 1) * form.getPageSize())
                        .limit(form.getPageSize())
                        .collect(Collectors.toList())
        );
        affiliateMembersPage.setTotal(affiliateTeamMembers.size());
        affiliateMembersPage.setPages(affiliateTeamMembers.size() / form.getPageSize());

        return PageResult.success(affiliateMembersPage);
    }

    @Operation(summary = "取得登入者資訊")
    @GetMapping("/me")
    public Result<H5AffiliateVO> getMe() {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.getH5AffiliateVO(affiliateId));
    }

    @Operation(summary = "前台-更新代理密碼")
    @PostMapping("/change/password")
    public Result<Boolean> changePassword(@Validated @RequestBody H5ChangePasswordForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        String newPassword = form.getNewPassword();
        String originPassword = form.getOriginPassword();

        return Result.success(iAffiliateService.changePassword(affiliateId, originPassword, newPassword));
    }

    @Operation(summary = "前台-更新代理提款密碼")
    @PostMapping("/change/withdraw/password")
    public Result<Boolean> changeWithdrawPassword(@Validated @RequestBody H5ChangePasswordForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        String newPassword = form.getNewPassword();
        String originPassword = form.getOriginPassword();
        return Result.success(iAffiliateService.changeWithdrawPassword(affiliateId, originPassword, newPassword));
    }

    @Operation(summary = "檢查有無代理帳號")
    @GetMapping("/check/{username}")
    public Result<Boolean> checkAffiliateUsername(@PathVariable("username") String username) {
        return Result.success(iAffiliateService.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .isPresent());
    }

    @Operation(summary = "找代理密碼找回方式")
    @GetMapping("/forgot/password/recover/way/{username}")
    public Result<AffiliateForgotPasswordRecoverWayVO> findAffiliateForgotPasswordRecoverWay(@PathVariable("username") String username) {
        AffiliateForgotPasswordRecoverWayVO affiliateForgotPasswordRecoverWay = AffiliateForgotPasswordRecoverWayVO.builder().build();

        iAffiliateProtectPasswordQuestionService.lambdaQuery()
                .eq(AffiliateProtectPasswordQuestion::getUsername, username)
                .oneOpt()
                .ifPresent(affiliate -> affiliateForgotPasswordRecoverWay.setProtectPassword(1));

        iAffiliateService.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .ifPresent(affiliate -> {
                            affiliateForgotPasswordRecoverWay.setMobile(affiliate.getMobile());
                            affiliateForgotPasswordRecoverWay.setEmail(affiliate.getEmail());
                        }
                );

        return Result.success(affiliateForgotPasswordRecoverWay);
    }

    @Operation(summary = "代理忘記密碼手機找回", description = "發送驗證碼至手機")
    @GetMapping("/forgot/password/mobile/{username}")
    @Limit(limitType = LimitType.IP, prefix = "findAffiliateForgotPasswordMobile", period = 60, count = 1)
    public Result<Boolean> findAffiliateForgotPasswordMobile(@PathVariable("username") String username) {
        return Result.success(iAffiliateService.findAffiliateForgotPasswordMobile(username));
    }

    @Operation(summary = "代理忘記密碼手機找回", description = "驗證手機驗證碼")
    @PostMapping("/forgot/password/mobile/{username}")
    public Result<Boolean> verifyAffiliateForgotPasswordMobile(@PathVariable("username") String username, @Validated @RequestBody VerifyAffiliateForgotPasswordForm form) {
        return Result.success(iAffiliateService.verifyAffiliateForgotPasswordMobile(username, form));
    }

    @Operation(summary = "代理忘記密碼手機找回", description = "手機找回-修改密碼")
    @PutMapping("/forgot/password/mobile/{username}")
    public Result<Boolean> modifyAffiliateForgotPasswordMobile(@PathVariable("username") String username, @Validated @RequestBody ModifyAffiliateForgotPasswordForm form) {
        return Result.success(iAffiliateService.modifyAffiliateForgotPassword(username, form));
    }

    @Operation(summary = "代理忘記密碼信箱找回", description = "發送驗證信")
    @GetMapping("/forgot/password/email/{username}")
    @Limit(limitType = LimitType.IP, prefix = "findAffiliateForgotPasswordEmail", period = 60, count = 1)
    public Result<Boolean> findAffiliateForgotPasswordEmail(@PathVariable("username") String username) {
        return Result.success(iAffiliateService.findAffiliateForgotPasswordEmail(username));
    }

    @Operation(summary = "代理忘記密碼信箱驗證", description = "驗證信箱驗證碼")
    @PostMapping("/forgot/password/email/{username}")
    public Result<Boolean> verifyAffiliateForgotPasswordEmail(@PathVariable("username") String username, @Validated @RequestBody VerifyAffiliateForgotPasswordForm form) {
        return Result.success(iAffiliateService.verifyAffiliateForgotPasswordEmail(username, form));
    }

    @Operation(summary = "代理忘記密碼手機找回", description = "信箱找回-修改密碼")
    @PutMapping("/forgot/password/email/{username}")
    public Result<Boolean> modifyAffiliateForgotPasswordEmail(@PathVariable("username") String username, @Validated @RequestBody ModifyAffiliateForgotPasswordForm form) {
        return Result.success(iAffiliateService.modifyAffiliateForgotPassword(username, form));
    }

    @Operation(summary = "真實姓名修改", description = "真實姓名修改")
    @PutMapping("/update/realname/{realname}")
    public Result<Boolean> modifyRealName(@PathVariable("realname") String realname) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.modifyAffiliateRealName(affiliateId, realname));
    }

    @Operation(summary = "遊戲帳號修改", description = "遊戲帳號修改")
    @PutMapping("/update/memberUsername/{memberUsername}")
    public Result<Boolean> modifyGameAccount(@PathVariable("memberUsername") String memberUsername) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.modifyMemberUsername(affiliateId, memberUsername));
    }

    @Operation(summary = "im帳號修改", description = "im帳號修改")
    @PutMapping("/update/im")
    public Result<Boolean> modifyImAccount(@RequestBody ModifyImForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.modifyIMAccount(affiliateId, form.getImType(), form.getIm()));
    }

    @Operation(summary = "代理手機綁定", description = "代理手機綁定")
    @PutMapping("/bind/verify/mobile")
    public Result<Boolean> bindMobile(@RequestBody @Validated VerifyMobileForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.modifyAffiliateMobile(affiliateId, form.getVerifyCode(), form.getMobile()));
    }

    @Operation(summary = "代理email綁定", description = "代理email綁定")
    @PutMapping("/bind/verify/email")
    public Result<Boolean> bindEmail(@RequestBody @Validated VerifyEmailForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.modifyAffiliateEmail(affiliateId, form.getVerifyCode(), form.getEmail()));
    }

    @Operation(summary = "代理信箱發送驗證信", description = "代理信箱發送驗證信")
    @GetMapping("/verify/email/{email}")
    @Limit(limitType = LimitType.IP, prefix = "sentVerifyCodeToEmail", period = 60, count = 1)
    public Result<String> sentVerifyCodeToEmail(@PathVariable("email") String email) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.getVerifyCodeByEmail(affiliateId, email));
    }

    @Operation(summary = "代理手機發送驗證碼", description = "代理發送驗證碼至手機")
    @GetMapping("/verify/mobile/{mobile}")
    @Limit(limitType = LimitType.IP, prefix = "sentVerifyCodeToMobile", period = 60, count = 1)
    public Result<String> sentVerifyCodeToMobile(@PathVariable("mobile") String mobile) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (affiliateId == null) {
            throw new BizException(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return Result.success(iAffiliateService.getVerifyCodeBySMS(affiliateId, mobile));
    }

    @Operation(summary = "下級遊戲記錄", description = "查詢下級遊戲記錄")
    @GetMapping("/playRecord")
    public PageResult<H5MemberPlayingRecordVO> getMemberPlayRecord(@ParameterObject SearchMemberPlayingRecordForm form) {
        IPage<H5MemberPlayingRecordVO> playingRecords = iMemberPlayRecordService.findMemberPlayRecord(form);
        if (playingRecords.getTotal() == 0) {
            return PageResult.success(new Page<>(form.getPageNum(), form.getPageSize()));
        }

        return PageResult.success(playingRecords);
    }

    @Operation(summary = "玩家存款記錄", description = "查詢玩家存款記錄, 會檢核此玩家上級是否為該登入代理")
    @GetMapping("/depositRecord")
    public PageResult<MemberDepositDTO> getMemberDepositRecord(@ParameterObject SearchMemberDepositRecordForm form) {
        return Optional.ofNullable(iMemberPlayRecordService.findMemberDepositRecord(form))
                .orElseThrow(() -> new BizException(ResultCode.MEMBER_NOT_EXIST_AFFILIATE_BELOW));

        //return null;
    }

}
