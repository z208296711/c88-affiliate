package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(title = "代理或玩家輸贏報表查詢表單")
public class SearchAffiliateWinLossForm extends BasePageQuery {

    @Schema(title = "代理或玩家帳號")
    private String username;

    @NotNull(message = "起始時間不得為空")
    @Schema(title = "起始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "結束時間不得為空")
    @Schema(title = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
