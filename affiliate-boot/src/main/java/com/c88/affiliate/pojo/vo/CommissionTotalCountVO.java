package com.c88.affiliate.pojo.vo;

import lombok.Data;

@Data
public class CommissionTotalCountVO {
    int level;
    int verifyCount;
    int verifiedCount;
    Integer status;
}
