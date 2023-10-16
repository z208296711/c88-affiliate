package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminAffiliateVO {

    @Schema(title = "代理id")
    private Long id;

    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "代理層級")
    private Integer level;

    @Schema(title = "可用餘額")
    private BigDecimal balance = BigDecimal.ZERO;

    @Schema(title = "佣金群組Id")
    private Long commissionGroupId;

    @Schema(title = "佣金群組")
    private String commissionGroup;

    @Schema(title = "推廣碼")
    private String promotionCode;

    @Schema(title = "上級代理")
    private String parentUsername;

    @Schema(title = "下級人數")
    private Integer subordinateCount = 0;

    @Schema(title = "代理連結")
    private String link = "";

    @Schema(title = "狀態啟用")
    private Integer enable;

    @Schema(title = "備註")
    private String note;

    @Schema(title = "電話")
    private String mobile;

    @Schema(title = "信箱")
    private String email;

    @Schema(title = "代理轉帳 0:停用, 1:啟用")
    private Integer affiliateTransferState;

    @Schema(title = "玩家轉帳： 0:停用, 1:啟用")
    private Integer memberTransferState;

    @Schema(title = "IM類型 0:Zalo, 1:FB Message")
    private Integer imType;

    @Schema(title = "IM帳號")
    private String im;

    @Schema(title = "會員帳號")
    private String memberUsername;

}
