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
@Schema(title = "存款詳情")
public class AffiliateFinancialStatementPersonalRechargeVO {

    @Parameter(description = "支付方式(i18n)")
    private String rechargeTypeName;

    @Parameter(description = "金額")
    private BigDecimal amount;

}
