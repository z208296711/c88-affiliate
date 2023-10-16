package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理會員標籤")
public class LabelVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "會員數量")
    private Long memberQuantity;

    @Schema(title = "標籤名稱")
    private String name;

}
