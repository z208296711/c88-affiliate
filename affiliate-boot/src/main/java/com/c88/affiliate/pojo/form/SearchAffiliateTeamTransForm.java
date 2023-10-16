package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(title = "代理團隊佣金表單")
public class SearchAffiliateTeamTransForm extends BasePageQuery {

    @Schema(title = "佣金月份，前端使用")
    private String date;
    @Schema(title = "代理帳號，前端使用")
    private String agentNameBelow;
    @Schema(title = "上級帳號")
    private List<Long> parentIds;

    @Schema(title = "起始時間")
    private LocalDateTime startTime;

    @Schema(title = "結束時間")
    private LocalDateTime endTime;

}
