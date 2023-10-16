package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "財務報表個人")
public class AffiliateFinancialStatementPersonalVO {

    @Schema(title = "存款")
    private BigDecimal recharge;

    @Schema(title = "存款詳情")
    private List<AffiliateFinancialStatementPersonalRechargeVO> rechargeDetail;

    @Schema(title = "提款")
    private BigDecimal withdraw;

    @Schema(title = "紅利")
    private BigDecimal bonus;

    @Schema(title = "紅利詳情")
    private List<AffiliateFinancialStatementPersonalBonusVO> bonusDetail;

    @Schema(title = "存送優惠")
    private BigDecimal rechargeAward;

    @Schema(title = "場館費")
    private BigDecimal platform;

    @Schema(title = "場館費詳情")
    private List<AffiliateFinancialStatementPersonalPlatformVO> platformDetail;

    @Schema(title = "總輸贏")
    private BigDecimal totalWinLoss;

    @Schema(title = "流水")
    private BigDecimal bet;

    @Schema(title = "總輸贏詳情")
    private List<AffiliateFinancialStatementPersonalTotalWinLossVO> totalWinLossDetail;

    @Schema(title = "淨輸贏")
    private BigDecimal netWinLoss;

    @Schema(title = "帳戶調整")
    private BigDecimal accountAdjustment;

}
