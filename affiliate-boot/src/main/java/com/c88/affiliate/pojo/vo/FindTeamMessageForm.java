package com.c88.affiliate.pojo.vo;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "查詢團隊訊息表單")
public class FindTeamMessageForm extends BasePageQuery {

    @Parameter(description = "代理帳號")
    @Schema(title = "代理帳號")
    private String username;

    @Parameter(description = "搜尋日期類型 0=全部 1=今日 2=昨日 3=本月 4=上月", example = "0")
    @Schema(title = "搜尋日期類型", description = "0=全部 1=今日 2=昨日 3=本月 4=上月", example = "0")
    private Integer searchTimeType;

}
