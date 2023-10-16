package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AffiliateCommissionTotalStateEnum {
    NOT_YET(0, "未確認"),
    CONFIRMED(1, "已確認"),
    ISSUED(2, "已發放");

    private final Integer code;

    private final String label;

    public static AffiliateCommissionTotalStateEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
