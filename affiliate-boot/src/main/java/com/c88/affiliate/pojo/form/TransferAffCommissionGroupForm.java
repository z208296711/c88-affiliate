package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "轉移代理佣金群組表單")
public class TransferAffCommissionGroupForm {

    @NotNull(message = "欲轉移群組ID不得為空")
    @Schema(title = "欲轉移群組ID")
    private Long sourceGroupId;

    @NotNull(message = "轉移目標群組ID不得為空")
    @Schema(title = "轉移目標群組ID")
    private Long targetGroupId;

}
