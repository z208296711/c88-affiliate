package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.c88.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 調整前期差額記錄表
 */
@TableName(value ="aff_difference_record")
@Data
public class DifferenceRecord extends BaseEntity {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理帳號Id
     */
    @TableField(value = "affiliate_id")
    private Long affiliateId;

    /**
     * 代理帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * 調整前缺額
     */
    @TableField(value = "before_amount")
    private BigDecimal beforeAmount;

    /**
     * 異動值
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 調整後缺額
     */
    @TableField(value = "after_amount")
    private BigDecimal afterAmount;

    /**
     * 備註
     */
    @TableField(value = "note")
    private String note;

    /**
     * 操作者
     */
    @TableField(value = "modified_by")
    private String modifiedBy;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}