package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AffMemberTransactionsTypeEnum {

    RECHARGE(1, "存款"),
    WITHDRAW(2, "提款"),
    SETTLE(3, "派彩"),
    UN_SETTLE(4, "未派彩"),
    CANCEL(5, "取消"),
    BONUS(6, "紅利"),









    NULL(0, "無");

    private final Integer code;

    private final String label;

    public static AffMemberTransactionsTypeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
