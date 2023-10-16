package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(title = "ModifyImForm")
public class ModifyImForm {

    @NotBlank
    @Schema(title = "imType")
    private Integer imType;

    @NotBlank
    @Schema(title = "im")
    private String im;


}
