package com.c88.affiliate.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(title = "會員的代理關聯")
public class AffiliateInfoDTO {

    /**
     * 代理ID
     */
    private Long id;

    /**
     * 代理帳號
     */
    private String username;

    /**
     * 代理-真實姓名
     */
    private String realName;

    /**
     * 代理-提款密码
     */
    private String withdrawPassword;

    /**
     * 代理-階層
     */
    private String parents;

    //基本資料最後修改時間
    private LocalDateTime lastInfoModified;



}
