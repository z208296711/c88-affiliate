package com.c88.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ElasticMemberTransEnum {
    BET("validBetAmount", "betType", "sum_bet_amount"),
    RECHARGE("rechargeAmount", "depositType","sum_recharge_amount"),
    RECHARGEAWARD("rechargeAwardAmount", "depositType","sum_recharge_award_amount"),
    WITHDRAW("withdrawAmount","withdrawType","sum_withdraw_amount"),
    BONUS("awardAmount","bonusType","sum_award_amount"),
    BET_WINLOSS("winLoss", "betType", "sum_win_amount");

    private final String fieldName;

    private final String filterName;

    private final String sumName;

    public Map<String, String> getFilterInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("filter", this.filterName);
        map.put("sum", this.sumName);
        return map;
    }

    public SumAggregationBuilder getSumBuilder(){
        return AggregationBuilders
                .sum(this.sumName)
                .field(this.fieldName);
    }

}
