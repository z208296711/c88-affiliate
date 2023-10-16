package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AffiliateForgotPasswordRecoverWayEnum {

    PROTECT_PASSWORD(1, "安全密保"),
    MOBILE(2, "手機號"),
    EMAIL(3, "郵箱");

    private final Integer code;

    private final String label;

    public static AffiliateForgotPasswordRecoverWayEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }


}
