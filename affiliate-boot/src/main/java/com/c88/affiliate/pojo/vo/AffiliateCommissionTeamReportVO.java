package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class AffiliateCommissionTeamReportVO {
    public AffiliateCommissionTeamReportVO(){

    }

    /**
     * 代理id
     */
    @JsonIgnore
    @Schema(title = "代理id")
    private Long agentId;
    @Schema(title = "帳務月份")
    private String verifyDate;

    @Schema(title = "代理帳號")
    private String agentUsername;

    @Schema(title="有效新增會員")
    private Integer validMembers = 0;

    /**
     * 活躍會員數
     */
    @Schema(title = "活躍會員數")
    private Integer activeMembers = 0;

    @Schema(title = "下級人數")
    private Long bottomMembers = 0L;

//
//    /**
//     * 會員行銷費用
//     */
//    @Schema(title = "會員行銷費用")
//    private BigDecimal marketingFee;


    /**
     * 代理淨盈利
     */
    @Schema(title = "代理淨盈利")
    private BigDecimal netProfit = BigDecimal.ZERO;

    @Schema(title = "前期缺額")
    private BigDecimal lastDifference = BigDecimal.ZERO;

    @Schema(title = "沖正後淨輸贏")
    private BigDecimal netWinLoss = BigDecimal.ZERO;

    public BigDecimal calcNetWinLoss(){
        return netProfit.subtract(lastDifference);
    }

    /**
     * 佣金比例
     */
    @Schema(title = "佣金比例")
    private BigDecimal commissionRate;

    @Schema(title = "佣金調整")
    private BigDecimal agentAdjust = BigDecimal.ZERO;

    /**
     * 代理佣金
     */
    @Schema(title = "佣金")
    private BigDecimal currentCommission = BigDecimal.ZERO;

    /**
//     * 狀態 0審核中 1已審核
//     */
//    @Schema(title = "0審核中 1已審核")
//    private Integer status;

//    /**
//     * 本期缺額
//     */
//    @Schema(title = "本期缺額")
//    private BigDecimal difference;
//
//    /**
//     * 累計缺額
//     */
//    @Schema(title = "累計缺額")
//    private BigDecimal totalDifference;
//
    /**
     * 佣金總計
     */
    @Schema(title = "佣金總計")
    private BigDecimal totalCommission;

    @Schema(title = "輸贏總計")
    private BigDecimal totalWinLoss;

//
//    /**
//     * 上級代理id
//     */
//    private Long parentId;
//
//    @Schema(title = "代理層級")
//    private int level;
//
//    @Schema(title = "上級代理帳號")
//    private String parentUsername;

//    @Schema(title="佣金發放狀態(0未發放 1已發放)")
//    private Integer state;
}
