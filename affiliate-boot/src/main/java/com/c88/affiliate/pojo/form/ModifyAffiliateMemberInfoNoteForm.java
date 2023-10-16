package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "會員詳情編輯備註表單")
public class ModifyAffiliateMemberInfoNoteForm {

    @NotNull(message = "會員帳號不得為空")
    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "備註")
    private String note;

}
