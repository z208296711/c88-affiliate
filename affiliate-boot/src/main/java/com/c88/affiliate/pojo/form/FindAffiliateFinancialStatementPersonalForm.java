package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "個人財務報表表單")
public class FindAffiliateFinancialStatementPersonalForm {

    @Parameter(description = "統計開始時間")
    @Schema(title = "統計開始時間")
    private String startTime;

    @Parameter(description = "統計結束時間")
    @Schema(title = "統計結束時間")
    private String endTime;

}
