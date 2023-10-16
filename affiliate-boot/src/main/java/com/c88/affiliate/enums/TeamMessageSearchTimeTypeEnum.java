package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 團隊訊息 搜尋日期類型
 */
@Getter
@AllArgsConstructor
public enum TeamMessageSearchTimeTypeEnum {

    ALL(0, "全部"),
    TODAY(1, "今日"),
    YESTERDAY(2, "昨日"),
    CURRENT_MONTH(3, "本月"),
    LAST_MONTH(4, "上月");

    private final Integer code;

    private final String label;

    public static TeamMessageSearchTimeTypeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }


}
