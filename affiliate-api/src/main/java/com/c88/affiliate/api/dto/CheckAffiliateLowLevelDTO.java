package com.c88.affiliate.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CheckAffiliateLowLevelDTO {

    @Schema(title = "代理帳號")
    private String parentUsername;

    @Schema(title = "遊戲帳號")
    private String memberUsername;

}