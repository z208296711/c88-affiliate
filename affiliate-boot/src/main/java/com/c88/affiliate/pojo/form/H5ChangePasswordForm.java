package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class H5ChangePasswordForm {

    @Schema(title = "原始密碼")
    private String originPassword;

    @Schema(title = "確認密碼")
    private String confirmPassword;

    @NotBlank
    @Schema(title = "新密碼")
    private String newPassword;

}