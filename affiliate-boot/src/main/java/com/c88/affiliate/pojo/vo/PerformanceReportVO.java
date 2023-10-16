package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "績效報表")
public class PerformanceReportVO {

    @Schema(title = "代理ID")
    private Long parentId;

    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "帳務月份")
    private String month;

    @Schema(title = "審核狀態")
    private Integer status;

    @Schema(title = "代理層級")
    private Integer level;

    @Schema(title = "活躍會員數")
    private Integer activityMember;

    @Schema(title = "會員虧損負盈利")
    private BigDecimal memberLossNegativeProfit;

    @Schema(title = "會員行銷費用")
    private BigDecimal memberSellAmount;

    @Schema(title = "場館使用費")
    private BigDecimal platformAmount;

    @Schema(title = "代理淨盈利")
    private BigDecimal parentProfit;

    @Schema(title = "佣金比例")
    private BigDecimal parentScale;

    @Schema(title = "代理佣金")
    private BigDecimal parentCommission;

    @Schema(title = "填補缺額")
    private BigDecimal fill;

    @Schema(title = "本期缺額")
    private BigDecimal periodFill;

    @Schema(title = "佣金總計")
    private BigDecimal parentCommissionTotal;
}
