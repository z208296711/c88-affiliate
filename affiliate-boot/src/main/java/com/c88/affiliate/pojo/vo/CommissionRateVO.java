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
@Schema(title = "佣金比例")
public class CommissionRateVO {

    @Schema(title = "階級")
    private Integer level;

    @Schema(title = "值")
    private BigDecimal value;
}
