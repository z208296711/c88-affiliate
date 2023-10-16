package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordForm {

    @NotBlank
    @Schema(title = "密碼")
    private String password;

}