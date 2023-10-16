package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "新增代理標籤表單")
public class AddAffiliateLabelForm {

    @Schema(title = "標籤")
    private String name;

}
