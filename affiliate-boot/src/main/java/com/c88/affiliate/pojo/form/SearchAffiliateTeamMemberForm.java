package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(title = "團隊管理表單")
public class SearchAffiliateTeamMemberForm extends BasePageQuery {

    @Schema(title = "代理帳號查詢")
    private String parentUsername;

    @Schema(title = "會員帳號查詢")
    private String username;

    @Schema(title = "最低存款金額")
    private BigDecimal minRecharge;

    @Schema(title = "最高存款金額")
    private BigDecimal maxRecharge;

    @Schema(title = "起始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @Schema(title = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @Schema(title = "註冊起始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerStartTime;

    @Schema(title = "註冊結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerEndTime;

}
