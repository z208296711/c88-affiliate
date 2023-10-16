package com.c88.affiliate.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "會員的代理關聯")
public class AffiliateMemberDTO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "類型")
    private Integer type;

    @Schema(title = "總代理ID")
    private Long masterId;

    @Schema(title = "總代理帳號")
    private String masterUsername;

    @Schema(title = "代理ID")
    private Long parentId;

    @Schema(title = "代理帳號")
    private String parentUsername;

    @Schema(title = "代理層級關係")
    private String parents;

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String memberUsername;

}
