package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SwiftAffiliateForm {

    @Schema(title = "上級代理查詢")
    private String parentUsername;

    @Schema(title = "會員帳號查詢")
    private List<Long> memberIds;

}