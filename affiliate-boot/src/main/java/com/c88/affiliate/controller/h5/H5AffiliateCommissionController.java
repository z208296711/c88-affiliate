package com.c88.affiliate.controller.h5;

import com.alibaba.excel.EasyExcelFactory;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionPlatformRecord;
import com.c88.affiliate.pojo.form.FindPerformanceReportExportForm;
import com.c88.affiliate.pojo.form.FindPerformanceReportForm;
import com.c88.affiliate.pojo.form.SearchAffiliateTeamTransForm;
import com.c88.affiliate.pojo.vo.AffiliateCommissionTeamReportVO;
import com.c88.affiliate.pojo.vo.AffliateCommissionPersonReportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportExportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportVO;
import com.c88.affiliate.service.IAffAffiliateCommissionRecordService;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "『前台』-佣金報表")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/affiliate/commission")
public class H5AffiliateCommissionController {
    private final IAffAffiliateCommissionRecordService affAffiliateCommissionRecordService;

    @Operation(summary = "個人佣金報表")
    @GetMapping("/report/personal/{date}")
    public Result<AffliateCommissionPersonReportVO> getPersonalReport(@PathVariable String date) {
        AffliateCommissionPersonReportVO personalCommission = affAffiliateCommissionRecordService.getPersonalCommission(date);
        return Result.success(personalCommission);
    }

    @Operation(summary = "團隊佣金報表")
    @GetMapping("/report/team/{date}")
    public PageResult<AffiliateCommissionTeamReportVO> getTeamReport(@ParameterObject SearchAffiliateTeamTransForm form) {
        return affAffiliateCommissionRecordService.getTeamCommission(form);
    }

    @Operation(summary = "查看帳單名細")
    @GetMapping("/report/personal/{date}/detail")
    public Result<List<AffAffiliateCommissionPlatformRecord>> getPlatformDetail(@PathVariable String date) {
        return Result.success(affAffiliateCommissionRecordService.getPlatformDetail(date));
    }

    @Operation(summary = "找績效報表")
    @GetMapping("/performance/report")
    public PageResult<PerformanceReportVO> findPerformanceReport(@ParameterObject FindPerformanceReportForm form) {
        return PageResult.success(affAffiliateCommissionRecordService.findPerformanceReport(form));
    }

    @Operation(summary = "匯出績效報表")
    @PostMapping("/performance/report/export")
    public void exportPerformanceReport(HttpServletResponse response,
                                        @RequestBody FindPerformanceReportExportForm form) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = URLEncoder.encode("agent_performance_" + date, StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try {
            EasyExcelFactory.write(response.getOutputStream(), PerformanceReportExportVO.class)
                    .sheet()
                    .doWrite(affAffiliateCommissionRecordService.exportPerformanceReport(form));
        } catch (IOException e) {
            log.error("export agent_performance fail");
        }
    }

}
