package com.c88.affiliate.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateMemberWithdrawDTO implements Serializable {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "提款單號")
    private String no;

    @Schema(title = "提款金額")
    private BigDecimal amount;

    @Schema(title = "備註")
    private String note;

}
