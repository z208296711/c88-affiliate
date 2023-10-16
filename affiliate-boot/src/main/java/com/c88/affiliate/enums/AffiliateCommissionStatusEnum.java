package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AffiliateCommissionStatusEnum {
    VERIFYING(0, "審核中"),
    PASS(1, "通過"),
    FAIL(2, "不通過"),
    ACTIVE_MEMBER_NOT_ENOUGH(3, "活躍人數不足"),
    UNDER_ISSUE(4, "未達發放標準");

    private final Integer code;

    private final String label;

    public static AffiliateCommissionStatusEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
