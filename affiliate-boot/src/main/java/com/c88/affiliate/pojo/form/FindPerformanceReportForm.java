package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "查詢績效報表表單")
public class FindPerformanceReportForm extends BasePageQuery {

    @Parameter(description = "月份")
    @Schema(title = "月份")
    private String month;

    @Parameter(description = "層級")
    @Schema(title = "層級")
    private Integer level;

    @Parameter(description = "狀態")
    @Schema(title = "狀態")
    private Integer state;

    @Parameter(description = "代理帳號")
    @Schema(title = "代理帳號")
    private String username;


}
