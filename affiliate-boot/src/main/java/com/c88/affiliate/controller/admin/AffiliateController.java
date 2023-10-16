package com.c88.affiliate.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.api.dto.AffiliateInfoDTO;
import com.c88.affiliate.api.dto.AuthAffiliateDTO;
import com.c88.affiliate.api.dto.CheckAffiliateLowLevelDTO;
import com.c88.affiliate.converter.AffiliateOperationLogConverter;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateOperationLog;
import com.c88.affiliate.pojo.form.AffiliateForm;
import com.c88.affiliate.pojo.form.ChangePasswordForm;
import com.c88.affiliate.pojo.form.SearchAffiliateForm;
import com.c88.affiliate.pojo.vo.AdminAffiliateVO;
import com.c88.affiliate.pojo.vo.AffiliateOperationLogVO;
import com.c88.affiliate.service.IAffiliateOperationLogService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.base.BasePageQuery;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.UserUtils;
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

@Tag(name = "『後台』-代理")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/affiliate")
public class AffiliateController {

    private final IAffiliateService iAffiliateService;

    private final IAffiliateOperationLogService iAffiliateOperationLogService;

    private final AffiliateOperationLogConverter affiliateOperationLogConverter;

    @Operation(summary = "for auth 做登入調用", hidden = true)
    @GetMapping("/username/{username}")
    public Result<AuthAffiliateDTO> getAffiliateByUsername(@PathVariable String username) {
        return Result.success(iAffiliateService.getAuthAffiliateDTO(username));
    }

    @GetMapping("/info/affiliateId/{id}")
    public Result<AffiliateInfoDTO> getAffiliateInfoById(@PathVariable Long id) {
        AffiliateInfoDTO affiliateInfoDTO = iAffiliateService.getAffiliateInfoDTO(id);
        if (affiliateInfoDTO == null) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }
        return Result.success(affiliateInfoDTO);
    }

    @GetMapping("/info/username/{username}")
    public Result<AffiliateInfoDTO> getAffiliateInfoByUsername(@PathVariable String username) {
        AffiliateInfoDTO affiliateInfoDTO = iAffiliateService.getAffiliateInfoDTO(username);
        if (affiliateInfoDTO == null) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }
        return Result.success(affiliateInfoDTO);
    }

    @Operation(summary = "後台-新增代理")
    @PostMapping
    public Result<Boolean> addAffiliate(@Validated @RequestBody AffiliateForm form) {
        return Result.success(iAffiliateService.createAffiliate(form).getId() != null);
    }

    @Operation(summary = "後台-判斷該會員是否為該代理的下級會員 (true 為是 , false為否)")
    @PostMapping("/check/level/low/member")
    public Result<Boolean> isAffiliateLowLevel(@Validated @RequestBody CheckAffiliateLowLevelDTO form) {
        return Result.success(iAffiliateService.isAffiliateLowLevel(form.getParentUsername(), form.getMemberUsername()));
    }

    @Operation(summary = "檢查 是否可新增 上級代理帳號")
    @GetMapping("/check/{username}")
    public Result<Boolean> checkAffiliateUsername(@PathVariable("username") String username) {
        Affiliate affiliate = iAffiliateService.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));
        if (affiliate.getLevel() == 2) {
            throw new BizException(ResultCode.AFFILIATE_CAN_NOT_ADD_LEVEL);
        }
        return Result.success(true);
    }

    @Operation(summary = "確認 email 是否已被綁定")
    @GetMapping("/check/email/{email}")
    public Result<Boolean> checkAffiliateEmail(@PathVariable String email) {
        int count = iAffiliateService.lambdaQuery().eq(Affiliate::getEmail, email).count();
        return Result.success(count == 0);
    }

    @Operation(summary = "確認 mobile 是否已被綁定")
    @GetMapping("/check/mobile/{mobile}")
    public Result<Boolean> checkAffiliateMobile(@PathVariable String mobile) {
        int count = iAffiliateService.lambdaQuery().eq(Affiliate::getMobile, mobile).count();
        return Result.success(count == 0);
    }

    @Operation(summary = "後台-設置代理")
    @PutMapping
    public Result<Boolean> editAffiliate(@Validated @RequestBody AffiliateForm form) {
        iAffiliateService.editAffiliate(form, UserUtils.getUsername());
        return Result.success(true);
    }

    @Operation(summary = "後台-更新代理密碼")
    @PostMapping("/{affiliateId}/change/password")
    public Result<Boolean> changePassword(@PathVariable Long affiliateId, @Validated @RequestBody ChangePasswordForm form) {
        return Result.success(iAffiliateService.changePassword(affiliateId, form.getPassword()));
    }

    @Operation(summary = "後台-更新代理提款密碼")
    @PostMapping("/{affiliateId}/change/withdraw/password")
    public Result<Boolean> changeWithdrawPassword(@PathVariable Long affiliateId, @Validated @RequestBody ChangePasswordForm form) {
        return Result.success(iAffiliateService.changeWithdrawPassword(affiliateId, form.getPassword()));
    }

    @Operation(summary = "後台查詢代理")
    @GetMapping
    public PageResult<AdminAffiliateVO> findAffiliatePage(@ParameterObject SearchAffiliateForm form) {
        return PageResult.success(iAffiliateService.findAffiliatePage(form));
    }

    @Operation(summary = "取得單一代理資訊")
    @GetMapping("/{id}")
    public Result<AdminAffiliateVO> getAdminAffiliateVO(@PathVariable Long id) {
        return Result.success(iAffiliateService.findAffiliate(id));
    }

    @Operation(summary = "取得代理異動紀錄")
    @GetMapping("/{id}/operation/log")
    public PageResult<AffiliateOperationLogVO> getAffiliateOperationLogPage(@PathVariable Long id,
                                                                            @ParameterObject BasePageQuery basePageQuery) {
        IPage<AffiliateOperationLogVO> affiliateOperationLogIPage = iAffiliateOperationLogService.lambdaQuery()
                .eq(AffiliateOperationLog::getAffiliateId, id)
                .orderByDesc(AffiliateOperationLog::getGmtCreate)
                .page(new Page<>(basePageQuery.getPageNum(), basePageQuery.getPageSize()))
                .convert(affiliateOperationLogConverter::toVO);
        return PageResult.success(affiliateOperationLogIPage);
    }

    @Operation(summary = "取得代理異動紀錄", hidden = true)
    @PutMapping(value = "/{affiliateId}/{realName}")
    public Result<Boolean> modifyAffiliateRealName(@PathVariable Long affiliateId, @PathVariable String realName) {
        Affiliate affiliate = iAffiliateService.getById(affiliateId);
        affiliate.setRealName(realName);
        return Result.success(iAffiliateService.updateById(affiliate));
    }
}
