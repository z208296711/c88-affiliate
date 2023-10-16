package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PerformanceReportEnum {

    NOT_REVIEW(1, "未審核"),
    REVIEW_SUCCESS(2, "審核通過"),
    REVIEW_FAIL(3, "審核未通過"),
    REVIEW_NOT_STANDARD(4, "未達發放標準"),
    ACTIVITY_MEMBER_INSUFFICIENT(5, "活躍人數不足"),
    ISSUED(6, "已發放");

    private final Integer code;

    private final String label;

    public static PerformanceReportEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }

}
