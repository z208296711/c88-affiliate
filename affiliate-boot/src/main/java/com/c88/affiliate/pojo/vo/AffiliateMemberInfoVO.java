package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "會員管理-會員詳情")
public class AffiliateMemberInfoVO {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "活躍狀態", description = "0非活躍 1活躍")
    private Integer activeState;

    @Schema(title = "存款")
    private BigDecimal recharge;

    @Schema(title = "提款")
    private BigDecimal withdraw;

    @Schema(title = "紅利")
    private BigDecimal bonus;

    @Schema(title = "存送優惠")
    private BigDecimal rechargeAward;

    @Schema(title = "有效投注")
    private BigDecimal validBet;

    @Schema(title = "總投注")
    private BigDecimal bet;

    @Schema(title = "帳戶調整")
    private BigDecimal accountAdjustment;

    @Schema(title = "總輸贏")
    private BigDecimal winLoss;

    @Schema(title = "註冊時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    @Schema(title = "首存時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstRechargeTime;

    @Schema(title = "會員標籤")
    private List<String> memberLabelNames;

    @Schema(title = "會員備註")
    private String memberNote;

    @Schema(title = "會員平台訊息")
    private List<AffiliateMemberPlatformDetailVO> platformInfos;

}
