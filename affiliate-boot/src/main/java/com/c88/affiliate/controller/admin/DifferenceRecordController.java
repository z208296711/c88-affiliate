package com.c88.affiliate.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.converter.DifferenceRecordConverter;
import com.c88.affiliate.enums.AffiliateCommissionTotalStateEnum;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionTotalRecord;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.DifferenceRecord;
import com.c88.affiliate.pojo.vo.AddDifferenceRecordForm;
import com.c88.affiliate.pojo.vo.DifferenceRecordVO;
import com.c88.affiliate.service.IAffAffiliateCommissionTotalRecordService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.affiliate.service.IDifferenceRecordService;
import com.c88.common.core.base.BasePageQuery;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.DateUtil;
import com.c88.common.web.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "『後台』-前期差額")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/difference/record")
public class DifferenceRecordController {

    private final IAffiliateService iAffiliateService;

    private final IDifferenceRecordService iDifferenceRecordService;

    private final DifferenceRecordConverter differenceRecordConverter;

    private final IAffAffiliateCommissionTotalRecordService affAffiliateCommissionTotalRecordService;

    @Operation(summary = "查找-前期差額")
    @GetMapping
    public PageResult<DifferenceRecordVO> getDifferenceRecord(@RequestParam(value = "username", required = false) String username,
                                                              @ParameterObject BasePageQuery basePageQuery) {
        return PageResult.success(iDifferenceRecordService
                .lambdaQuery()
                .eq(StringUtils.isNotBlank(username), DifferenceRecord::getUsername, username)
                .orderByDesc(DifferenceRecord::getGmtCreate)
                .page(new Page<>(basePageQuery.getPageNum(), basePageQuery.getPageSize()))
                .convert(differenceRecordConverter::toVO));
    }

    @Operation(summary = "新增-前期差額")
    @PostMapping
    public Result<Boolean> modifyDifferenceRecord(@RequestBody AddDifferenceRecordForm form) {
        return Result.success(iAffiliateService.modifyDifferenceRecord(form));
    }


    @Operation(summary = "查找-該用戶的前期差額")
    @GetMapping("/{username}")
    public Result<BigDecimal> getAffiliateDifference(@PathVariable("username") String username) {

        Affiliate affiliate = iAffiliateService.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .select(Affiliate::getDifference)
                .oneOpt().orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthDateTime = now.minusMonths(1);
        String lastMonth = DateUtil.ym_df.format(lastMonthDateTime);
        List<AffAffiliateCommissionTotalRecord> lastRecords = affAffiliateCommissionTotalRecordService.lambdaQuery().eq(AffAffiliateCommissionTotalRecord::getVerifyDate, lastMonth).list();
        if(lastRecords.size()==0 ||  !lastRecords.get(0).getStatus().equals(AffiliateCommissionTotalStateEnum.ISSUED.getCode())){
//            return Result.failedByCode("Z0012");
            throw new BizException(ResultCode.AFFILIATE_COMMISSION_NOT_ISSUE_YET_FOR_ADJUST);
        }
        return Result.success(affiliate.getDifference());
    }

}
