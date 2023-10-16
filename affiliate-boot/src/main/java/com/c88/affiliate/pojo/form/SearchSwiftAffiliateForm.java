package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchSwiftAffiliateForm extends BasePageQuery {

    @Parameter(description = "上級代理查詢")
    private String parentUsername;

    @Parameter(description = "會員帳號查詢")
    private String memberUsername;

    @Parameter(description = "金額下限")
    private BigDecimal minWinLoss;

    @Parameter(description = "金額上限")
    private BigDecimal maxWinLoss;

    @Parameter(description = "會員ID", hidden = true)
    private List<Long> memberIdList;

    @Parameter(description = "開始時間")
    private LocalDateTime startTime;

    @Parameter(description = "結束時間")
    private LocalDateTime endTime;

}