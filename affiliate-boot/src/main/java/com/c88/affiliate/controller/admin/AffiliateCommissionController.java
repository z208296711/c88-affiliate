package com.c88.affiliate.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.enums.AffiliateCommissionTotalStateEnum;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionTotalRecord;
import com.c88.affiliate.pojo.form.AffiliateCommissionForm;
import com.c88.affiliate.service.IAffAffiliateCommissionRecordService;
import com.c88.affiliate.service.IAffAffiliateCommissionTotalRecordService;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.DateUtil;
import com.c88.common.web.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Tag(name = "『後台』-代理佣金審核單")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/affiliate/commission")
public class AffiliateCommissionController {

    private final IAffAffiliateCommissionRecordService affAffiliateCommissionRecordService;
    private final IAffAffiliateCommissionTotalRecordService affAffiliateCommissionTotalRecordService;

    @Operation(summary = "佣金審核單")
    @GetMapping("/list/{date}")
    public Result getCommissionList(@PathVariable String date){
        return Result.success(affAffiliateCommissionTotalRecordService.getCountList(date));
    }

    @Operation(summary = "產生佣金審核單")
    @PostMapping("/create")
    public Result<Integer> getAffiliateByUsername(@RequestBody AffiliateCommissionForm form) {
        LocalDateTime now = DateUtil.strToFirstDayOfMonthWithLocalDateTime(form.getDate());
        LocalDateTime lastMonthDateTime = now.minusMonths(1);
        String lastMonth = DateUtil.ym_df.format(lastMonthDateTime);
        List<AffAffiliateCommissionTotalRecord> lastRecords = affAffiliateCommissionTotalRecordService.lambdaQuery().eq(AffAffiliateCommissionTotalRecord::getVerifyDate, lastMonth).list();
        if(lastRecords.size()==0 ||  !lastRecords.get(0).getStatus().equals(AffiliateCommissionTotalStateEnum.ISSUED.getCode())){
//            throw new BizException(ResultCode.AFFILIATE_COMMISSION_NOT_ISSUE_YET);
            throw new BizException("be_Agent_audit.Btn04_info_errorMessage");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() ->
                affAffiliateCommissionRecordService.createCommission(2, form.getDate()));
        int result = affAffiliateCommissionRecordService.createCommission(form.getLevel(), form.getDate());
        executor.shutdown();
        return Result.success(result);
    }

    @Operation(summary = "瀏覽")
    @GetMapping("/list")
    public PageResult<AffAffiliateCommissionRecord> getCommissionList(@ParameterObject AffiliateCommissionForm form){
        Page<AffAffiliateCommissionRecord> page = affAffiliateCommissionRecordService.lambdaQuery()
                .eq(AffAffiliateCommissionRecord::getVerifyDate, form.getDate())
                .eq(AffAffiliateCommissionRecord::getLevel, form.getLevel())
                .eq(form.getUsername()!=null, AffAffiliateCommissionRecord::getAgentUsername, form.getUsername())
                .eq(form.getStatus()!=null, AffAffiliateCommissionRecord::getStatus, form.getStatus())
                .page(new Page<>(form.getPageNum(), form.getPageSize()));
        return PageResult.success(page);
    }

    @Operation(summary = "審核單筆")
    @PutMapping("/verify/{id}/{status}")
    public Result<Boolean> verify(@PathVariable Long id, @PathVariable int status){
        return Result.success(affAffiliateCommissionRecordService.verifyCommission(id, status));
    }

    @Operation(summary = "確認審核")
    @PutMapping("/verifyall/{date}/{level}")
    public Result<Boolean> verifyAll(@PathVariable String date, @PathVariable int level){
        return Result.success(affAffiliateCommissionTotalRecordService.completeLevel(date, level));
    }

    @Operation(summary = "重新計算")
    @PutMapping("/reset/{date}/{level}")
    public Result<Boolean> reset(@PathVariable String date, @PathVariable int level){
        return Result.success(affAffiliateCommissionTotalRecordService.resetLevel(date, level));
    }

    @Operation(summary = "發送佣金")
    @PostMapping("/issue")
    public Result<Boolean> issue(@RequestBody AffiliateCommissionForm form){
        List<AffAffiliateCommissionTotalRecord> totalRecords = affAffiliateCommissionTotalRecordService.lambdaQuery().eq(AffAffiliateCommissionTotalRecord::getVerifyDate, form.getDate()).list();
        if(totalRecords.size()>0 && totalRecords.get(0).getStatus().equals(AffiliateCommissionTotalStateEnum.ISSUED.getCode()))
            throw new BizException(ResultCode.AFFILIATE_COMMISSION_SENT);
        return Result.success(affAffiliateCommissionRecordService.issueCommission(form.getDate()));
    }
}
