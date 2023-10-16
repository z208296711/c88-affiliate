package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
@Getter
@AllArgsConstructor
public enum AffiliateCommissionStateEnum {
    NOT_YET(0, "未發放"),
    ISSUED(1, "已發放");

    private final Integer code;

    private final String label;

    public static AffiliateCommissionStateEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
