package com.c88.affiliate.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerformanceReportExportVO {

    @ExcelProperty(value = "代理帳號")
    private String username;

    @ExcelProperty(value = "帳務月份")
    private String month;

    @ExcelProperty(value = "審核狀態")
    private String status;

    @ExcelProperty(value = "代理層級")
    private Integer level;

    @ExcelProperty(value = "活躍會員數")
    private Integer activityMember;

    @ExcelProperty(value = "會員虧損負盈利")
    private BigDecimal memberLossNegativeProfit;

    @ExcelProperty(value = "會員行銷費用")
    private BigDecimal memberSellAmount;

    @ExcelProperty(value = "場館使用費")
    private BigDecimal platformAmount;

    @ExcelProperty(value = "代理淨盈利")
    private BigDecimal parentProfit;

    @ExcelProperty(value = "佣金比例")
    private BigDecimal parentScale;

    @ExcelProperty(value = "代理佣金")
    private BigDecimal parentCommission;

    @ExcelProperty(value = "填補缺額")
    private BigDecimal fill;

    @ExcelProperty(value = "本期缺額")
    private BigDecimal periodFill;

    @ExcelProperty(value = "佣金總計")
    private BigDecimal parentCommissionTotal;
}
