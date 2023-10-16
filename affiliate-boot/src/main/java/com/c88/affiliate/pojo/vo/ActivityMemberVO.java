package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "活躍玩家")
public class ActivityMemberVO {

    @Schema(title = "階級")
    private Integer level;

    @Schema(title = "值")
    private Integer value;
}
