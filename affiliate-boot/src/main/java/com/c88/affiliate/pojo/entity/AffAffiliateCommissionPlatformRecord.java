package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName aff_affiliate_commission_platform_record
 */
@TableName(value ="aff_affiliate_commission_platform_record")
@Data
@Builder
public class AffAffiliateCommissionPlatformRecord implements Serializable {
    /**
     * 發放日期
     */
    private String issueDate;

    /**
     * 代理id
     */
    private Long agentId;

    /**
     * 平台名稱
     */
    private String platformName;

    /**
     * 總輸贏
     */
    private BigDecimal totalWinloss;

    /**
     * 場館費
     */
    private BigDecimal platformFee;

    /**
     * 金額
     */
    private BigDecimal amount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}