package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(title = "VerifyEmailForm")
public class VerifyMobileForm {

    @NotBlank
    @Schema(title = "驗證碼")
    private String verifyCode;

    @NotBlank
    @Schema(title = "mobile")
    private String mobile;


}
