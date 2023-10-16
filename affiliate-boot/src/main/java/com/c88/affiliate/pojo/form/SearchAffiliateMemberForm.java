package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(title = "代理會員管理表單")
public class SearchAffiliateMemberForm extends BasePageQuery {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號查詢")
    private String username;

    @Schema(title = "最低存款金額")
    private BigDecimal minRecharge;

    @Schema(title = "最高存款金額")
    private BigDecimal maxRecharge;

    @NotNull(message = "起始時間不得為空")
    @Schema(title = "起始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @NotNull(message = "結束時間不得為空")
    @Schema(title = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @Schema(title = "註冊起始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerStartTime;

    @Schema(title = "註冊結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerEndTime;

    @Schema(title = "會員標籤", description = "0=全部 -1=未分配")
    private List<Long> labels;

}
