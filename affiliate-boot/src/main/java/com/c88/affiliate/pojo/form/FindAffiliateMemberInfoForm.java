package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Schema(title = "會員管理-會員詳情表單")
public class FindAffiliateMemberInfoForm {

    @NotNull(message = "會員帳號不得為空")
    @Schema(title = "會員帳號")
    private String username;

    @NotNull(message = "統計開始時間不得為空")
    @Schema(title = "統計開始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @NotNull(message = "統計結束時間不得為空")
    @Schema(title = "統計結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

}
