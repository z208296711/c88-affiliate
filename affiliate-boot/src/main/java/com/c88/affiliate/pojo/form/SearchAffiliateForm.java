package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SearchAffiliateForm extends BasePageQuery {

    @Schema(title = "佣金群組ID")
    private Integer commissionGroupId;

    @Schema(title = "1:啟用, 0:凍結")
    private Integer enable;

    @Schema(title = "代理層級")
    private Integer level;

    @Schema(title = "代理帳號查詢")
    private String username;

    @Schema(title = "代理線查詢")
    private String parentUsername;

    @Schema(title = "推廣碼查詢")
    private String promotionCode;

    @Schema(title = "真實姓名查詢")
    private String realName;


}
