package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "會員管理")
public class H5AffiliateMemberVO {

    @Schema(title = "會員信息")
    private String username;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "vip等級名稱")
    private String vipName;

    @Schema(title = "存款")
    private BigDecimal recharge;

    @Schema(title = "提款")
    private BigDecimal withdrawal;

    @Schema(title = "總輸贏")
    private BigDecimal winLoss;

    @Schema(title = "註冊時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    @Schema(title = "最後登入時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(title = "標籤清單")
    private List<String> labels;

}
