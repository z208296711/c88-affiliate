package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AffliateCommissionPersonReportVO {
    @Schema(title = "帳務月份")
    private String verifyDate;

    /**
     * 活躍會員數
     */
    @Schema(title = "活躍會員數")
    private Integer activeMembers;

    @Schema(title="有效新增會員")
    private Integer validMembers;

    @Schema(title = "總輸贏")
//    @JsonIgnore
    private BigDecimal totalWinLoss;

    /**
     * 場館費
     */
    @Schema(title = "場館費")
    private BigDecimal platformFee;

    @Schema(title = "紅利")
    private BigDecimal awardAmount;

    @Schema(title="存送優惠")
    private BigDecimal rechargeAwardAmount;

    @Schema(title = "帳戶調整")
    private BigDecimal memberAdjust;



//    /**
//     * 會員虧損負盈利
//     */
//    @Schema(title = "淨輸贏")
//    private BigDecimal memberWinloss;
//
//    /**
//     * 會員行銷費用
//     */
//    @Schema(title = "會員行銷費用")
//    private BigDecimal marketingFee;


    /**
     * 代理淨盈利
     */
    @Schema(title = "淨輸贏")
    private BigDecimal netProfit;

    @Schema(title = "前期缺額")
    private BigDecimal lastDifference;

    @Schema(title = "沖正後淨輸贏")
    private BigDecimal netWinLoss;

//    private BigDecimal getNetWinLoss(){
//        return netProfit.subtract(lastDifference);
//    }

    /**
     * 佣金比例
     */
    @Schema(title = "佣金比例")
    private BigDecimal commissionRate;

    @Schema(title = "佣金調整")
    private BigDecimal agentAdjust;

//    /**
//     * 代理佣金
//     */
//    @Schema(title = "佣金")
//    private BigDecimal currentCommission;

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

    @Schema(title="佣金發放狀態(0未發放 1已發放)")
    private Integer state;

    @Schema(title = "發放時間")
    private LocalDateTime issueDate;

    @Schema(title = "狀態 0審核中 1審核通過 2不通過 3活躍人數不足 4未達發放標準")
    private Integer status;
}
