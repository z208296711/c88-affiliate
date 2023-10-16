package com.c88.affiliate.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class AffTransactionCountVO {
    Long agentId = 0L;
    BigDecimal winLoss = BigDecimal.ZERO;
    BigDecimal rechargeAwardAmount = BigDecimal.ZERO;
    BigDecimal awardAmount = BigDecimal.ZERO;
    BigDecimal platformFee = BigDecimal.ZERO;
}
