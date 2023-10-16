package com.c88.affiliate.pojo.vo;

import com.c88.common.core.desensitization.annotation.Sensitive;
import com.c88.common.core.desensitization.enums.DesensitizedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NotNull
@AllArgsConstructor
@Schema(title = "代理密碼找回方式")
public class AffiliateForgotPasswordRecoverWayVO {

    @Schema(title = "密保", description = "1有")
    private Integer protectPassword;

    @Sensitive(strategy = DesensitizedType.MOBILE_PHONE)
    @Schema(title = "手機號")
    private String mobile;

    @Sensitive(strategy = DesensitizedType.EMAIL)
    @Schema(title = "郵箱")
    private String email;

}
