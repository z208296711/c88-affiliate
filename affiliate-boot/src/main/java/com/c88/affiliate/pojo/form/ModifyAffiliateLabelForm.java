package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "修改代理標籤表單")
public class ModifyAffiliateLabelForm {

    @NotNull(message = "ID不得為空")
    @Schema(title = "ID")
    private Long id;

    @Schema(title = "標籤名稱")
    private String name;

}
