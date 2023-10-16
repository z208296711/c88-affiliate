package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AffiliateOperationTypeEnum {
    //異動類型 0:設置代理層級類型, 1:設為第一層代理 2:更改上級代理為 3:修改佣金群組為 4:修改手機號碼為 5:修改電子郵箱為 6:修改綁定遊戲帳號為 7:修改真實姓名為 8:修改IM類型 9:編輯停/啟用
    SETTING_LEVEL(0, "設置代理層級類型"),
    SETTING_FIRST_LEVEL(1, "設為第一層代理"),
    EDIT_PARENT(2, "更改上級代理為"),
    EDIT_COMMISSION_GROUP(3, "修改佣金群組為"),
    EDIT_MOBILE(4, "修改手機號碼為"),
    EDIT_EMAIL(5, "修改電子郵箱為"),
    EDIT_MEMBER(6, "修改綁定遊戲帳號為"),
    EDIT_REAL_NAME(7, "修改真實姓名為"),
    EDIT_IM(8, "修改IM類型"),
    EDIT_ENABLE(9, "編輯停/啟用"),
    EDIT_ADMIN_BALANCE(10, "管理員充值，替{}加值{}");

    private final Integer value;

    private final String label;

    public static AffiliateOperationTypeEnum getEnum(Integer value) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getValue(), value)).findFirst().orElseThrow();
    }


}
