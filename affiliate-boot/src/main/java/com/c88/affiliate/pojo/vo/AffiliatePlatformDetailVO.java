package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliatePlatformDetailVO {
    @JsonIgnore
    String platformCode;

    String platformName;
    BigDecimal winLoss;
    BigDecimal platformFee;
    BigDecimal totalAmount;

}
