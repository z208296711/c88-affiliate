package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "新增佣金群組組合表單")
public class AddOrModifyCommissionGroupForm {

    @Schema(title = "參數級距設置")
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
