package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "驗證密保問題表單")
public class ValidAffiliateProtectPasswordQuestionForm {

    @NotNull(message = "答案1不得為空")
    @Schema(title = "答案1")
    private String answer1;

    @NotNull(message = "答案2不得為空")
    @Schema(title = "答案2")
    private String answer2;

    @NotNull(message = "答案3不得為空")
    @Schema(title = "答案3")
    private String answer3;

}
