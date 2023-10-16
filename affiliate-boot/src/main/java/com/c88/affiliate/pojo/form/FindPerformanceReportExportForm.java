package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "匯出績效報表表單")
public class FindPerformanceReportExportForm {

    @Schema(title = "月份")
    private String month;

    @Schema(title = "層級")
    private Integer level;

    @Schema(title = "狀態")
    private Integer state;

    @Schema(title = "代理帳號")
    private String username;

}
