package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterAffiliateForm {

    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "代理密碼")
    private String password;

    @Schema(title = "確認密碼")
    private String confirmPassword;

    @Schema(title = "google token 驗證")
    private String token;


}