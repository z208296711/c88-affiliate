package com.c88.affiliate.config.sharding;

import com.c88.common.core.util.DateUtil;
import com.google.common.collect.Range;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.c88.common.core.constant.DateConstants.FORMAT_LINK_MONTH;

/**
 * 日期分表策略
 */
public class CreateTimeMonthTableShardingAlgorithm implements StandardShardingAlgorithm<LocalDateTime>, CreateTimeShardingAlgorithm {

    //范围查询处理
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames,
                                         RangeShardingValue<LocalDateTime> shardingValue) {
        Range<LocalDateTime> valueRange;
        valueRange = shardingValue.getValueRange();
        LocalDateTime start = null;
        try {
            start = valueRange.lowerEndpoint();
        } catch (Exception e) {
            start = LocalDateTime.now().minusMonths(3L);
        }
        LocalDateTime end = null;
        try {
            end = valueRange.upperEndpoint();
        } catch (Exception e) {
            end = LocalDateTime.now();
        }
        Set<String> suffixList = new HashSet<>();
        Iterator<String> iterator = availableTargetNames.iterator();
        String tableName = iterator.next();
        String name = tableName.substring(0, tableName.lastIndexOf("_"));
        if (start != null && end != null) {
            String startName = DateUtil.dateToStr(start, FORMAT_LINK_MONTH);
            String endName = DateUtil.dateToStr(end, FORMAT_LINK_MONTH);
            while (!startName.equals(endName)) {
                if (availableTargetNames.contains(name + "_" + startName)) {
                    suffixList.add(name + "_" + startName);
                }
                start = start.plusDays(1L);
                startName = DateUtil.dateToStr(start, FORMAT_LINK_MONTH);

            }
            if (availableTargetNames.contains(name + "_" + endName)) {
                suffixList.add(name + "_" + endName);
            }
        }
        if (CollectionUtils.isNotEmpty(suffixList)) {
            return suffixList;
        }

        return availableTargetNames;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public Properties getProps() {
        return null;
    }

    //精确查询处理
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<LocalDateTime> shardingValue) {
        LocalDateTime time = shardingValue.getValue();
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern(FORMAT_LINK_MONTH);
        String format = dtf2.format(time);
        for (String str : availableTargetNames) {
            if (str.endsWith(format)) {
                return str;
            }
        }
        return null;
    }

    @Override
    public void init(Properties properties) {

    }

    @Override
    public String buildNodesSuffix(LocalDate date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(FORMAT_LINK_MONTH);
        return date.format(dateFormatter);
    }

    @Override
    public LocalDate buildNodesBeforeDate(LocalDate date) {
        return date.minusMonths(1);
    }

    @Override
    public LocalDate buildNodesAfterDate(LocalDate date) {
        return date.plusMonths(1);
    }
}
