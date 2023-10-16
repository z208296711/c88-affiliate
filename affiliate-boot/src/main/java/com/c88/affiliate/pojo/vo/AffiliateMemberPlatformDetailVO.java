package com.c88.affiliate.pojo.vo;

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
@Schema(title = "會員管理-會員詳情-遊戲平台訊息")
public class AffiliateMemberPlatformDetailVO {

    @Schema(title = "平台名稱")
    private String platformName;

    @Schema(title = "遊戲類型i18n")
    private String gameCategory;

    @Schema(title = "流水")
    private BigDecimal bet;

    @Schema(title = "輸贏")
    private BigDecimal winLoss;

}
