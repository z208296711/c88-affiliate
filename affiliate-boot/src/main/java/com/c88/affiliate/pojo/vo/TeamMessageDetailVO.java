package com.c88.affiliate.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "團隊訊息 詳細訊息")
public class TeamMessageDetailVO {

    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "下級人數")
    private Long memberCount;

    @Schema(title = "首存人數")
    private Long firstRechargeMemberCount;

    @Schema(title = "加入團隊時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTeamTime;

}
