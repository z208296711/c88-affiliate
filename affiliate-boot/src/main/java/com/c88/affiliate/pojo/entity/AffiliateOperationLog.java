package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 代理異動紀錄表
 *
 * @TableName aff_affiliate_operation_log
 */
@TableName(value = "aff_affiliate_operation_log", autoResultMap = true)
@Data
public class AffiliateOperationLog extends BaseEntity {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所屬代理id
     */
    @TableField(value = "affiliate_id")
    private Long affiliateId;

    /**
     * 代理名稱
     */
    @TableField(value = "affiliate_username")
    private String affiliateUsername;

    /**
     * 異動類型 0:設置代理層級類型, 1:設為第一層代理 2:更改上級代理為 3:修改佣金群組為 4:修改手機號碼為 5:修改電子郵箱為 6: 修改綁定遊戲帳號為 7:修改真實姓名為 8:修改IM類型 9:啟用1/停用0
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 異動內容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 操作者
     */
    @TableField(value = "update_by")
    private String updateBy;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}