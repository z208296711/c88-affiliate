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
@Schema(title = "總輸贏詳情")
public class AffiliateFinancialStatementPersonalTotalWinLossVO {

    @Parameter(description = "平台名稱")
    private String platformName;

    @Parameter(description = "遊戲類型(i18n)")
    private String gameType;

    @Parameter(description = "總輸贏")
    private BigDecimal winLoss;

    @Parameter(description = "場館費率")
    private BigDecimal platformRate;

    @Parameter(description = "場館費")
    private BigDecimal platformFee;

    @Parameter(description = "流水")
    private BigDecimal bet;

}
