package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName aff_affiliate_commission_record
 */
@TableName(value ="aff_affiliate_commission_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffAffiliateCommissionRecord implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理id
     */
    @Schema(title = "代理id")
    private Long agentId;

    /**
     * 代理帳號
     */
    @Schema(title = "代理帳號")
    private String agentUsername;

    /**
     * 發放年月
     */
    @Schema(title = "帳務月份")
    private String verifyDate;

    /**
     * 活躍會員數
     */
    @Schema(title = "活躍會員數")
    private Integer activeMembers;

    /**
     * 會員虧損負盈利
     */
    @Schema(title = "會員虧損負盈利")
    private BigDecimal memberWinloss;

    /**
     * 會員行銷費用
     */
    @Schema(title = "會員行銷費用")
    private BigDecimal marketingFee;

    /**
     * 場館費
     */
    @Schema(title = "場館費")
    private BigDecimal platformFee;

    /**
     * 代理淨盈利
     */
    @Schema(title = "代理淨盈利")
    private BigDecimal netProfit;

    /**
     * 佣金比例
     */
    @Schema(title = "佣金比例")
    private BigDecimal commissionRate;

    /**
     * 代理佣金
     */
    @Schema(title = "代理佣金")
    private BigDecimal currentCommission;

    /**
     * 狀態 0審核中 1已審核
     */
    @Schema(title = "狀態 0審核中 1審核通過 2不通過 3活躍人數不足 4未達發放標準")
    private Integer status;

    /**
     * 本期缺額
     */
    @Schema(title = "本期缺額")
    private BigDecimal difference;

    /**
     * 累計缺額
     */
    @Schema(title = "累計缺額")
    private BigDecimal totalDifference;

    /**
     * 佣金總計
     */
    @Schema(title = "佣金總計")
    private BigDecimal totalCommission;

    /**
     * 上級代理id
     */
    private Long parentId;

    @Schema(title = "代理層級")
    private int level;

    @Schema(title = "上級代理帳號")
    private String parentUsername;

    @Schema(title="佣金發放狀態(0未發放 1已發放)")
    private Integer state;

    @Schema(title = "紅利")
    private BigDecimal awardAmount;

    @Schema(title = "存送優惠")
    private  BigDecimal rechargeAwardAmount;

    @Schema(title = "上期缺額")
    private BigDecimal lastDifference;

    @Schema(title="發放時間")
    private LocalDateTime issueDate;

    @Schema(title = "審核狀態")
    private Integer verifyStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}