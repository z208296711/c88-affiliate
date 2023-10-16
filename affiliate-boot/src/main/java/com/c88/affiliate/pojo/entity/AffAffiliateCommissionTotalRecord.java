package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName aff_affiliate_commission_total_record
 */
@TableName(value ="aff_affiliate_commission_total_record")
@Data
@Builder
public class AffAffiliateCommissionTotalRecord implements Serializable {
    /**
     * 審核年月
     */
    private String verifyDate;

    /**
     * 代理層級
     */
    private Integer level;

    /**
     * 0未確認 1已確認
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}