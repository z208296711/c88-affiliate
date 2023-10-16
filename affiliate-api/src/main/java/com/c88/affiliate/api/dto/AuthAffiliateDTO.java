package com.c88.affiliate.api.dto;

import lombok.Data;

@Data
public class AuthAffiliateDTO {

    /**
     * 代理ID
     */
    private Long id;

    /**
     * 代理帳號
     */
    private String username;

    /**
     * 代理密码
     */
    private String password;

    /**
     * 代理-提款密码
     */
    private String withdrawPassword;

    /**
     * 代理狀態(1:正常;0:禁用)
     */
    private Integer enable;

}
