// package com.c88.affiliate.controller.admin;
//
// import com.alibaba.excel.EasyExcelFactory;
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.c88.affiliate.converter.PerformanceReportConverter;
// import com.c88.affiliate.pojo.entity.PerformanceReport;
// import com.c88.affiliate.pojo.form.FindPerformanceReportExportForm;
// import com.c88.affiliate.pojo.form.FindPerformanceReportForm;
// import com.c88.affiliate.pojo.vo.PerformanceReportExportVO;
// import com.c88.affiliate.pojo.vo.PerformanceReportVO;
// import com.c88.affiliate.service.IPerformanceReportService;
// import com.c88.common.core.result.PageResult;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springdoc.api.annotations.ParameterObject;
// import org.springframework.web.bind.annotation.*;
//
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// @Tag(name = "績效報表")
// @Slf4j
// @RestController
// @RequiredArgsConstructor
// @RequestMapping(path = "/api/v1/affiliate/performance/report")
// public class PerformanceReportController {
//
//     private final IPerformanceReportService iPerformanceReportService;
//
//     private final PerformanceReportConverter performanceReportConverter;
//
//     @Operation(summary = "找績效報表")
//     @GetMapping
//     public PageResult<PerformanceReportVO> findPerformanceReport(@ParameterObject FindPerformanceReportForm form) {
//         IPage<PerformanceReportVO> performanceReportPage = iPerformanceReportService.lambdaQuery()
//                 .eq(StringUtils.isNotBlank(form.getMonth()), PerformanceReport::getMonth, form.getMonth())
//                 .eq(Objects.nonNull(form.getLevel()), PerformanceReport::getLevel, form.getLevel())
//                 .eq(Objects.nonNull(form.getState()), PerformanceReport::getState, form.getState())
//                 .eq(StringUtils.isNotBlank(form.getUsername()), PerformanceReport::getUsername, form.getUsername())
//                 .page(new Page<>(form.getPageNum(), form.getPageSize()))
//                 .convert(performanceReportConverter::toVO);
//
//         return PageResult.success(performanceReportPage);
//     }
//
//     @Operation(summary = "匯出績效報表")
//     @PostMapping("/export")
//     public void exportPerformanceReport(HttpServletResponse response,
//                                         @RequestBody FindPerformanceReportExportForm form) {
//         String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//         String fileName = URLEncoder.encode("agent_performance_" + date, StandardCharsets.UTF_8);
//         response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//
//         List<PerformanceReportVO> performanceReports = iPerformanceReportService.lambdaQuery()
//                 .eq(StringUtils.isNotBlank(form.getMonth()), PerformanceReport::getMonth, form.getMonth())
//                 .eq(Objects.nonNull(form.getLevel()), PerformanceReport::getLevel, form.getLevel())
//                 .eq(Objects.nonNull(form.getState()), PerformanceReport::getState, form.getState())
//                 .eq(StringUtils.isNotBlank(form.getUsername()), PerformanceReport::getUsername, form.getUsername())
//                 .list()
//                 .stream()
//                 .map(performanceReportConverter::toVO)
//                 .collect(Collectors.toList());
//         try {
//             EasyExcelFactory.write(response.getOutputStream(), PerformanceReportExportVO.class)
//                     .sheet()
//                     .doWrite(performanceReports);
//         } catch (IOException e) {
//             log.error("export agent_performance fail");
//         }
//
//
//     }
//
// }
