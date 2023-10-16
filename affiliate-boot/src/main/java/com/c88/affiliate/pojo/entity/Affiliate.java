package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.affiliate.annotation.ForUpdate;
import com.c88.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代理帳號
 *
 * @TableName aff_affiliate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "aff_affiliate")
public class Affiliate extends BaseEntity {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理類型: 地推:0 , 大代理:1
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
     * 上級ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 上級代理帳號
     */
    @TableField(value = "parent_username")
    @ForUpdate(fieldName = "parentUsername")
    private String parentUsername;

    /**
     * 所有層級
     */
    @TableField(value = "parents")
    private String parents;

    /**
     * 層級
     */
    @TableField(value = "level")
    @ForUpdate(fieldName = "level")
    private Integer level;

    /**
     * 佣金群組ID
     */
    @TableField(value = "commission_group_id")
    private Long commissionGroupId;

    /**
     * 帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密碼
     */
    @TableField(value = "password")
    private String password;

    /**
     * 真實姓名
     */
    @TableField(value = "real_name")
    @ForUpdate(fieldName = "realName")
    private String realName;

    /**
     * 代理推薦碼
     */
    @TableField(value = "promotion_code")
    private String promotionCode;

    /**
     * 提款密碼
     */
    @TableField(value = "withdraw_password")
    private String withdrawPassword;

    /**
     * 郵箱
     */
    @TableField(value = "email")
    @ForUpdate(fieldName = "email")
    private String email;

    /**
     * 手機
     */
    @TableField(value = "mobile")
    @ForUpdate(fieldName = "mobile")
    private String mobile;

    /**
     * 備註
     */
    @TableField(value = "note")
    private String note;

    /**
     * 啟用狀態(0:停用, 1:啟用)
     */
    @TableField(value = "enable")
    @ForUpdate(fieldName = "enable")
    private Integer enable;

    /**
     * 代理轉帳 (0:停用, 1:啟用)
     */
    @TableField(value = "affiliate_transfer_state")
    private Integer affiliateTransferState;

    /**
     * 玩家轉帳 (0:停用, 1:啟用)
     */
    @TableField(value = "member_transfer_state")
    private Integer memberTransferState;

    /**
     * 註冊IP
     */
    @TableField(value = "register_ip")
    private String registerIp;

    /**
     * 最後登入IP
     */
    @TableField(value = "last_login_ip")
    private String lastLoginIp;

    /**
     * 最後登入時間
     */
    @TableField(value = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 綁定遊戲會員ID
     */
    @TableField(value = "member_id")
    @ForUpdate(fieldName = "memberId")
    private Long memberId;

    /**
     * 綁定遊戲會員帳號
     */
    @TableField(value = "member_username")
    @ForUpdate(fieldName = "memberUsername")
    private String memberUsername;

    /**
     * 社交媒體類型 0: zalo, 1:fb
     */
    @TableField(value = "im_type")
    @ForUpdate(fieldName = "imType")
    private Integer imType;

    /**
     * 社交媒體帳號
     */
    @TableField(value = "im")
    @ForUpdate(fieldName = "im")
    private String im;


    /**
     * 前期差額
     */
    @TableField(value = "difference")
    private BigDecimal difference;


    /**
     * 最後修改真實姓名或手機號的時間，用於風控計算
     */
    @TableField(value = "last_info_modified")
    private LocalDateTime lastInfoModified;


}
