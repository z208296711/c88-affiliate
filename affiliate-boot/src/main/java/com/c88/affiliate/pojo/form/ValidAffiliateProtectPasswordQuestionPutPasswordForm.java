package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "驗證密保問題成功後可修改密碼表單")
public class ValidAffiliateProtectPasswordQuestionPutPasswordForm {

    @NotNull(message = "答案1不得為空")
    @Schema(title = "答案1")
    private String answer1;

    @NotNull(message = "答案2不得為空")
    @Schema(title = "答案2")
    private String answer2;

    @NotNull(message = "答案3不得為空")
    @Schema(title = "答案3")
    private String answer3;

    @NotNull(message = "新密碼不得為空")
    @Schema(title = "新密碼")
    private String newPassword;

    @NotNull(message = "確認新密碼不得為空")
    @Schema(title = "確認新密碼")
    private String confirmPassword;

}
