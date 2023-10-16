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
@Schema(title = "佣金群組階級資訊")
public class CommissionGroupDetailVO {

    @Schema(title = "階級")
    private Integer level;

    @Schema(title = "每月活躍玩家")
    private Integer activityMember;

    @Schema(title = "每月最小盈利")
    private BigDecimal minProfit;

    @Schema(title = "每月最大盈利")
    private BigDecimal maxProfit;

    @Schema(title = "佣金比例")
    private BigDecimal rate;

}
