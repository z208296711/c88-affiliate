package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(title = "代轉移玩家")
public class SwiftMemberVO {

    @Schema(title = "會員帳號")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String memberUsername;

    @Schema(title = "上級代理")
    private String parentUsername;

    @Schema(title = "存款")
    private BigDecimal recharge = BigDecimal.ZERO;

    @Schema(title = "提款")
    private BigDecimal withdraw = BigDecimal.ZERO;

    @Schema(title = "總輸贏")
    private BigDecimal winLoss = BigDecimal.ZERO;

    @Schema(title = "註冊時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    @Schema(title = "最後登入時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
}
