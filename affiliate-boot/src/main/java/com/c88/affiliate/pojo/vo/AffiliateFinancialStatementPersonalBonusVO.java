package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "紅利詳情")
public class AffiliateFinancialStatementPersonalBonusVO {

    @Parameter(description = "優惠類型")
    private String bonusType;

    @Parameter(description = "金額")
    private BigDecimal amount;

}
