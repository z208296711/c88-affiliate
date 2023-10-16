package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "修改代理密碼表單", description = "手機 信箱")
public class ModifyAffiliateForgotPasswordForm {

    @Schema(title = "新密碼")
    private String newPassword;

    @Schema(title = "確認密碼")
    private String confirmPassword;

    @Schema(title = "確認驗證碼")
    private String verifyCode;


}
