package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "團隊財務報表表單")
public class FindAffiliateFinancialStatementTeamForm extends BasePageQuery {

    @Parameter(description = "代理帳號")
    @Schema(title = "代理帳號")
    private String username;

    @NotNull(message = "統計月份不得為空")
    @Parameter(description = "統計月份")
    @Schema(title = "統計月份")
    private String month;

}
