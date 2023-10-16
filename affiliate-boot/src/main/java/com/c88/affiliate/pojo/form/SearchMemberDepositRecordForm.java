package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "代理會員存款記錄表單")
public class SearchMemberDepositRecordForm extends BasePageQuery {

    @Schema(title = "玩家帳號")
    private String username;

    @Schema(title = "統計起始時間")
    private String depositStartTime;

    @Schema(title = "統計結束時間")
    private String depositEndTime;

}
