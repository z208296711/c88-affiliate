package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代理帳號標籤
 *
 * @TableName aff_affiliate_label
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "aff_label")
public class Label extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 標籤
     */
    @TableField(value = "name")
    private String name;

}