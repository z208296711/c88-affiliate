package com.c88.affiliate.pojo.vo;

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
@Schema(title = "財務報表個人")
public class FindAffiliateFinancialStatementTeamVO {

    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "存款")
    private BigDecimal recharge;

    @Schema(title = "提款")
    private BigDecimal withdraw;

    @Schema(title = "紅利")
    private BigDecimal bonus;

    @Schema(title = "存送優惠")
    private BigDecimal rechargeAward;

    @Schema(title = "場館費")
    private BigDecimal platform;

    @Schema(title = "總輸贏")
    private BigDecimal totalWinLoss;

    @Schema(title = "淨輸贏")
    private BigDecimal netWinLoss;

    @Schema(title = "總代理", description = "0否 1是")
    private Integer isCaptain;

}
