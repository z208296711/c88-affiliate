package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代理會員關聯表
 */
@TableName(value = "aff_affiliate_member")
@Data
public class AffiliateMember extends BaseEntity {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 類型
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 總代理ID
     */
    @TableField(value = "master_id")
    private Long masterId;

    /**
     * 總代理帳號
     */
    @TableField(value = "master_username")
    private String masterUsername;

    /**
     * 代理ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 代理帳號
     */
    @TableField(value = "parent_username")
    private String parentUsername;

    /**
     * 代理層級關係
     */
    @TableField(value = "parents")
    private String parents;

    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     * 會員帳號
     */
    @TableField(value = "member_username")
    private String memberUsername;

    /**
     * 啟用狀態(0:停用, 1:啟用)
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 備註
     */
    @TableField(value = "note")
    private String note;

    /**
     * 註冊時間
     */
    @TableField(value = "register_time")
    private LocalDateTime registerTime;

    /**
     * 最後登錄時間
     */
    @TableField(value = "last_login_time")
    private LocalDateTime lastLoginTime;

}