package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchSwiftAffiliateＭemberForm extends BasePageQuery {

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @Parameter(description = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @Schema(title = "時區，+X", example = "0(UTC), 8(Taipei)")
    private Integer gmtTime = 0;

}