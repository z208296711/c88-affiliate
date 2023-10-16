package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "新增會員標籤")
public class ModifyAffiliateMemberLabelForm {

    @Schema(title = "會員帳號")
    private List<String> usernames;

    @Schema(title = "標籤ID")
    private List<Long> labelIds;

}
