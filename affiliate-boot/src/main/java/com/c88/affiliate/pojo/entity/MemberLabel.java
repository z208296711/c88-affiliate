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
 * 代理會員標籤
 *
 * @TableName aff_member_label
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "aff_member_label")
public class MemberLabel extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 標籤ID
     */
    @TableField(value = "label_id")
    private Long labelId;

    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 會員帳號
     */
    @TableField(value = "username")
    private String username;

}