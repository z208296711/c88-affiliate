package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateWinLossVO {

    @Schema(title = "代理等級")
    private Integer agentLevel;

    @Schema(title = "代理佣金模式")
    private String commissionMode;

    @Schema(title = "代理或玩家帳號")
    private String username;

    @Schema(title = "註冊會員數")
    private Integer registerNum;

    @Schema(title = "首存人數")
    private Integer downPaymentNum;

    @Schema(title = "存款人數")
    private Integer depositNum;

    @Schema(title = "存款筆數")
    private Integer depositRowNum;

    @Schema(title = "存款金額")
    private BigDecimal depositAmount;

    @Schema(title = "提款人數")
    private Integer withdrawNum;

    @Schema(title = "提款筆數")
    private Integer withdrawRowNum;

    @Schema(title = "提款金額")
    private BigDecimal withdrawAmount;

    @Schema(title = "投注金額")
    private BigDecimal betAmount;

    @Schema(title = "存送優惠")
    private BigDecimal depositAward;

    @Schema(title = "時間內首存人數")
    private Integer periodDepositNum;

    @Schema(title = "時間內首存金額")
    private BigDecimal periodDepositAmount;

    @Schema(title = "手續費")
    private BigDecimal fee;

    @Schema(title = "公司淨盈利")
    private BigDecimal netProfit;

    @Schema(title = "紅利")
    private BigDecimal bonus = BigDecimal.ZERO;

    @Schema(title = "返水")
    private BigDecimal rebate = BigDecimal.ZERO;

}
