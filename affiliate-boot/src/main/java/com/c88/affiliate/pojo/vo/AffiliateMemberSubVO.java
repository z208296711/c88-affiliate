package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class AffiliateMemberSubVO {

    @Schema(title = "上級代理ID")
    private Long parentId;

    @Schema(title = "數量")
    private Integer count;

}
