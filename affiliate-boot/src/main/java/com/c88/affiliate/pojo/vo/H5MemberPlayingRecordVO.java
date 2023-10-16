package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "遊戲記錄")
public class H5MemberPlayingRecordVO {

    @Schema(title = "下級帳號")
    private String username;

    @Schema(title = "vip等級")
    private String vipName = "";

    @Schema(title = "平台")
    private String platformCode;

    @Schema(title = "類型")
    private String gameCategoryCode;
    @JsonIgnore
    @Schema(title = "遊戲ID")
    private String gameId;

    @Schema(title = "遊戲名稱")
    private String gameName;

    @Schema(title = "投注額")
    private BigDecimal betAmount;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;

    @Schema(title = "狀態")
    private String status;

    @Schema(title = "派彩")
    private BigDecimal settle;

    @Schema(title = "投注時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

}
