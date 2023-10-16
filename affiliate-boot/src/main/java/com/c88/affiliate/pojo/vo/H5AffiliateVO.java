package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理-個人資訊")
public class H5AffiliateVO {

    @Schema(title = "帳號")
    private String username;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "手機")
    private String mobile;

    @Schema(title = "信箱")
    private String email;

    @Schema(title = "0:未設定, 1:已設定")
    private Integer withdraw;

    @Schema(title = "IM類型 0:Zalo, 1:FB Message")
    private Integer imType;

    @Schema(title = "IM帳號")
    private String im;

    @Schema(title = "代理轉帳", description = "0:停用, 1:啟用")
    private Integer affiliateTransferState;

    @Schema(title = "玩家轉帳", description = "0:停用, 1:啟用")
    private Integer memberTransferState;

    @Schema(title = "推廣碼")
    private String promotionCode;

    @Schema(title = "下級成員數")
    private Integer subordinateCount = 0;

    @Schema(title = "遊戲帳號")
    private String memberUsername;

    @Schema(title = "密保", description = "0無 1有")
    private Integer protectPassword;

}
