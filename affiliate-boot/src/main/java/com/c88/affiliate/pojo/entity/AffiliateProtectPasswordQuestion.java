package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 代理保護密碼問題
 *
 * @TableName aff_affiliate_protect_password_question
 */
@TableName(value = "aff_affiliate_protect_password_question")
@Data
public class AffiliateProtectPasswordQuestion extends BaseEntity {
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
     * 代理帳號
     */
    @TableField(value = "username")
    private String username;

    /**
     * 問題1
     */
    @TableField(value = "question1")
    private String question1;

    /**
     * 答案1
     */
    @TableField(value = "answer1")
    private String answer1;

    /**
     * 問題2
     */
    @TableField(value = "question2")
    private String question2;

    /**
     * 答案2
     */
    @TableField(value = "answer2")
    private String answer2;

    /**
     * 問題3
     */
    @TableField(value = "question3")
    private String question3;

    /**
     * 答案3
     */
    @TableField(value = "answer3")
    private String answer3;

}