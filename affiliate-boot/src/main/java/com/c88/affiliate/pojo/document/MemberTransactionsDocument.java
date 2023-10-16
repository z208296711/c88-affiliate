package com.c88.affiliate.pojo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ES會員交易紀錄
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "member-transactions", createIndex = false)
public class MemberTransactionsDocument {

    @Id
    private Long id;

    /**
     * 會員ID
     */
    @Field(type = FieldType.Keyword)
    private Long memberId;

    /**
     * 會員帳號
     */
    @Field(type = FieldType.Keyword)
    private String memberUsername;

    /**
     * 總代理id
     */
    @Field(type = FieldType.Keyword)
    private Long masterId;

    /**
     * 總代理名稱
     */
    @Field(type = FieldType.Keyword)
    private String masterUsername;

    /**
     * 代理ID
     */
    @Field(type = FieldType.Keyword)
    private Long parentId;

    /**
     * 上級代理帳號
     */
    @Field(type = FieldType.Keyword)
    private String parentUsername;

    /**
     * 所有階層
     */
    @Field(type = FieldType.Wildcard)
    private String parents;

    /**
     * 1:存款 2:提款 3:投注輸贏
     */
    @Field(type = FieldType.Keyword)
    private Integer type;

    /**
     * 提款單號
     */
    @Field(type = FieldType.Keyword)
    private String withdrawNo;

    /**
     * 提款金額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal withdrawAmount;

    /**
     * 存款單號
     */
    @Field(type = FieldType.Keyword)
    private String tradeNo;

    /**
     * 注單號
     */
    @Field(type = FieldType.Keyword)
    private String transactionSerial;

    /**
     * 支付方式
     */
    @Field(type = FieldType.Keyword)
    private String payType;

    /**
     * 存送優惠獲得金額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal rechargeAwardAmount;

    /**
     * 充值金額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal rechargeAmount;

    /**
     * 實際到帳金額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal realAmount;

    /**
     * 平台代碼
     */
    @Field(type = FieldType.Keyword)
    private String platformCode;

    /**
     * 遊戲類型
     */
    @Field(type = FieldType.Keyword)
    private String gameCategoryCode;

    /**
     * 三方的遊戲ID
     */
    @Field(type = FieldType.Keyword)
    private String gameId;

    /**
     * 三方的遊戲名稱
     */
    @Field(type = FieldType.Keyword)
    private String gameName;

    /**
     * 該筆有效投注額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal validBetAmount;

    /**
     * 該筆投注額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal betAmount;

    /**
     * 該筆總派彩
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal settle;

    /**
     * 該筆總輸贏
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal winLoss;

    /**
     * 紅利金額
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal awardAmount;

    /**
     * 場館費
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal platformFee;

    /**
     * 萬國碼
     */
    @Field(type = FieldType.Keyword)
    private String i18n;

    /**
     * 備註
     */
    @Field(type = FieldType.Keyword)
    private String note;

    /**
     * 創建時間
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime gmtCreate;

    /**
     * 修改時間
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime gmtModified;

}