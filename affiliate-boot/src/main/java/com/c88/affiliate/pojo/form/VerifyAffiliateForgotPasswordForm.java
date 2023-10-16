package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "驗證忘記密碼找回表單")
public class VerifyAffiliateForgotPasswordForm {

    @NotNull(message = "驗證碼不得為空")
    @Schema(title = "驗證碼")
    private String code;


}
