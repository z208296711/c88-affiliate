package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理找回密碼-密保")
public class AffiliateProtectPasswordQuestionVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "問題1")
    private String question1;

    @Schema(title = "答案1")
    private String answer1;

    @Schema(title = "問題2")
    private String question2;

    @Schema(title = "答案2")
    private String answer2;

    @Schema(title = "問題3")
    private String question3;

    @Schema(title = "答案3")
    private String answer3;

}
