package com.c88.affiliate.controller.h5;

import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.CommissionGroup;
import com.c88.affiliate.pojo.vo.*;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.affiliate.service.ICommissionGroupService;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Tag(name = "『前台』佣金群組")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/affiliate/commission/group")
public class H5CommissionGroupController {

    private final IAffiliateService iAffiliateService;

    private final ICommissionGroupService iAffCommissionGroupService;

    @Operation(summary = "前台查找-佣金群組")
    @GetMapping
    public Result<List<CommissionGroupDetailVO>> findAffCommissionGroup() {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        Affiliate affiliate = Optional.ofNullable(iAffiliateService.getById(affiliateId))
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        CommissionGroup commissionGroup = iAffCommissionGroupService.getById(affiliate.getCommissionGroupId());
        return Result.success(commissionGroup.getDetails());
    }


}
