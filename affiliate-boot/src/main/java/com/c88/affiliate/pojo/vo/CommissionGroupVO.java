package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "佣金群組")
public class CommissionGroupVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "群組名稱")
    private String name;

    @Schema(title = "代理數量")
    private Integer affiliateQuantity;

    @Schema(title = "佣金群組階級資訊")
    private List<CommissionGroupDetailVO> details;

    @Schema(title = "每月活躍玩家")
    private List<ActivityMemberVO> activityMembers;

    @Schema(title = "每月最小盈利")
    private List<MinProfitVO> minProfits;

    @Schema(title = "每月最大盈利")
    private List<MaxProfitVO> maxProfits;

    @Schema(title = "佣金比例")
    private List<CommissionRateVO> rates;

}
