package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "新增密保表單")
public class AddAffiliateProtectPasswordQuestionForm {

    @NotNull(message = "問題1不得為空")
    @Schema(title = "問題1")
    private String question1;

    @NotNull(message = "答案1不得為空")
    @Schema(title = "答案1")
    private String answer1;

    @NotNull(message = "問題2不得為空")
    @Schema(title = "問題2")
    private String question2;

    @NotNull(message = "答案2不得為空")
    @Schema(title = "答案2")
    private String answer2;

    @NotNull(message = "問題3不得為空")
    @Schema(title = "問題3")
    private String question3;

    @NotNull(message = "答案3不得為空")
    @Schema(title = "答案3")
    private String answer3;

}
