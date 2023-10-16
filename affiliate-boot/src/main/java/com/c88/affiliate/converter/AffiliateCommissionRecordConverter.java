package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.c88.affiliate.pojo.vo.PerformanceReportExportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AffiliateCommissionRecordConverter {

    @Mappings({
            @Mapping(source = "agentUsername", target = "username"),
            @Mapping(source = "verifyDate", target = "month"),
            @Mapping(source = "activeMembers", target = "activityMember"),
            @Mapping(source = "memberWinloss", target = "memberLossNegativeProfit"),
            @Mapping(source = "marketingFee", target = "memberSellAmount"),
            @Mapping(source = "platformFee", target = "platformAmount"),
            @Mapping(source = "netProfit", target = "parentProfit"),
            @Mapping(source = "commissionRate", target = "parentScale"),
            @Mapping(source = "currentCommission", target = "parentCommission"),
            @Mapping(source = "lastDifference", target = "fill"),
            @Mapping(source = "difference", target = "periodFill"),
            @Mapping(source = "totalCommission", target = "parentCommissionTotal")
    })
    PerformanceReportVO toVO(AffAffiliateCommissionRecord entity);

    @Mappings({
            @Mapping(source = "agentUsername", target = "username"),
            @Mapping(source = "verifyDate", target = "month"),
            @Mapping(source = "activeMembers", target = "activityMember"),
            @Mapping(source = "memberWinloss", target = "memberLossNegativeProfit"),
            @Mapping(source = "marketingFee", target = "memberSellAmount"),
            @Mapping(source = "platformFee", target = "platformAmount"),
            @Mapping(source = "netProfit", target = "parentProfit"),
            @Mapping(source = "commissionRate", target = "parentScale"),
            @Mapping(source = "currentCommission", target = "parentCommission"),
            @Mapping(source = "lastDifference", target = "fill"),
            @Mapping(source = "difference", target = "periodFill"),
            @Mapping(source = "totalCommission", target = "parentCommissionTotal")
    })
    PerformanceReportExportVO toReportVO(AffAffiliateCommissionRecord entity);

    @BeforeMapping
    default void before(AffAffiliateCommissionRecord entity) {
        if (entity.getStatus() == 1 && entity.getState() == 1) {
            entity.setStatus(5);
        }
    }

    @AfterMapping
    default void reportMapping(@MappingTarget PerformanceReportExportVO vo) {
        Map<String, String> stateMap = Map.of(
                "0", "未審核",
                "1", "審核通過",
                "2", "審核未通過",
                "3", "活躍人數不足",
                "4", "未達發放標準",
                "5", "已發放"
        );

        vo.setStatus(stateMap.getOrDefault(vo.getStatus(), ""));
    }

}
