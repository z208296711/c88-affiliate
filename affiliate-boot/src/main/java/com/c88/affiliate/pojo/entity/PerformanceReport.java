package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 績效報表
 *
 * @TableName aff_performance_report
 */
@TableName(value = "aff_performance_report")
@Data
public class PerformanceReport extends BaseEntity {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 代理帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * 帳務月份
     */
    @TableField(value = "month")
    private String month;

    /**
     * 發放狀態 1未審核 2審核通過 3審核未通過 4未達發放標準 5活躍人數不足 6已發放
     */
    @TableField(value = "state")
    private Integer state;

    /**
     * 代理層級
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 活躍會員數
     */
    @TableField(value = "activityMember")
    private Integer activityMember;

    /**
     * 會員虧損負盈利
     */
    @TableField(value = "member_loss_negative_profit")
    private BigDecimal memberLossNegativeProfit;

    /**
     * 會員行銷費用
     */
    @TableField(value = "member_sell_amount")
    private BigDecimal memberSellAmount;

    /**
     * 場館使用費
     */
    @TableField(value = "platform_amount")
    private BigDecimal platformAmount;

    /**
     * 代理淨盈利
     */
    @TableField(value = "parent_profit")
    private BigDecimal parentProfit;

    /**
     * 佣金比例
     */
    @TableField(value = "parent_scale")
    private BigDecimal parentScale;

    /**
     * 代理佣金
     */
    @TableField(value = "parent_commission")
    private BigDecimal parentCommission;

    /**
     * 填補缺額
     */
    @TableField(value = "fill")
    private BigDecimal fill;

    /**
     * 本期缺額
     */
    @TableField(value = "period_fill")
    private BigDecimal periodFill;

    /**
     * 佣金總計
     */
    @TableField(value = "parent_commission_total")
    private BigDecimal parentCommissionTotal;

}