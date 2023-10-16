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

import java.math.BigDecimal;

/**
 * 代理會員交易紀錄表
 *
 * @TableName aff_member_transactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "aff_member_transactions")
public class AffMemberTransactions extends BaseEntity {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 會員ID
     */
    @TableField(value = "member_id")
    private Long memberId;

    /**
     *
     */
    @TableField(value = "member_username")
    private String memberUsername;

    /**
     * 總代理id
     */
    @TableField(value = "master_id")
    private Long masterId;

    /**
     * 總代理名稱
     */
    @TableField(value = "master_username")
    private String masterUsername;

    /**
     * 代理ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     *
     */
    @TableField(value = "parent_username")
    private String parentUsername;

    /**
     * 所有階層
     */
    @TableField(value = "parents")
    private String parents;

    /**
     * 1:存款 2:提款 3:投注輸贏
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 提款單號
     */
    @TableField(value = "withdraw_no")
    private String withdrawNo;

    /**
     * 提款金額
     */
    @TableField(value = "withdraw_amount")
    private BigDecimal withdrawAmount;

    /**
     * 存款單號
     */
    @TableField(value = "trade_no")
    private String tradeNo;

    /**
     * 注單號
     */
    @TableField(value = "transaction_serial")
    private String transactionSerial;

    /**
     * 支付方式
     */
    @TableField(value = "pay_type")
    private String payType;

    /**
     * 存送優惠獲得金額
     */
    @TableField(value = "recharge_award_amount")
    private BigDecimal rechargeAwardAmount;

    /**
     * 充值金額
     */
    @TableField(value = "recharge_amount")
    private BigDecimal rechargeAmount;

    /**
     * 實際到帳金額
     */
    @TableField(value = "real_amount")
    private BigDecimal realAmount;

    /**
     * 平台代碼
     */
    @TableField(value = "platform_code")
    private String platformCode;

    /**
     * 遊戲類型
     */
    @TableField(value = "game_category_code")
    private String gameCategoryCode;

    /**
     * 三方的遊戲ID
     */
    @TableField(value = "game_id")
    private String gameId;

    /**
     * 三方的遊戲名稱
     */
    @TableField(value = "game_name")
    private String gameName;

    /**
     * 該筆有效投注額
     */
    @TableField(value = "valid_bet_amount")
    private BigDecimal validBetAmount;

    /**
     * 該筆投注額
     */
    @TableField(value = "bet_amount")
    private BigDecimal betAmount;

    /**
     * 該筆總派彩
     */
    @TableField(value = "settle")
    private BigDecimal settle;

    /**
     * 該筆總輸贏
     */
    @TableField(value = "win_loss")
    private BigDecimal winLoss;

    /**
     * 紅利金額
     */
    @TableField(value = "award_amount")
    private BigDecimal awardAmount;

    /**
     * 場館費
     */
    @TableField(value = "platform_fee")
    private BigDecimal platformFee;

    /**
     * 萬國碼
     */
    @TableField(value = "i18n")
    private String i18n;

    /**
     * 備註
     */
    @TableField(value = "note")
    private String note;

}