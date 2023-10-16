package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class AffiliateForm {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "代理密碼")
    private String password;

    @Schema(title = "遊戲帳號")
    private String memberUsername;

    @Schema(title = "提款密碼")
    private String withdrawPassword;

    @Schema(title = "上級代理帳號")
    private String parentUsername;

    @Schema(title = "設定代理層級 1:第一層代理, 2:設定上級代理")
    private Integer level;

    @Schema(title = "代理佣金群組Id")
    private Long commissionGroupId;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "手機號碼")
    private String mobile;

    @Schema(title = "電子郵箱")
    private String email;

    @Schema(title = "IM類型 0:zalo, 1:FB Message")
    private Integer imType;

    @Schema(title = "IM帳號")
    @Size(max = 30)
    private String im;

    @Schema(title = "停/啟用")
    private Integer enable;

    @Schema(title = "代理轉帳", description = "0:停用, 1:啟用")
    private Integer affiliateTransferState;

    @Schema(title = "玩家轉帳", description = "0:停用, 1:啟用")
    private Integer memberTransferState;

    @Schema(title = "備註")
    private String note;

}