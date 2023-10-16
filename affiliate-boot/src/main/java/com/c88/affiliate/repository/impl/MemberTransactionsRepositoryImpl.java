package com.c88.affiliate.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.enums.ElasticMemberTransEnum;
import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import com.c88.affiliate.pojo.vo.AffiliateWinLossVO;
import com.c88.affiliate.pojo.vo.MemberWinLossVO;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.common.core.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MemberTransactionsRepositoryImpl implements IMemberTransactionsRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public MemberTransactionsDocument save(MemberTransactionsDocument entity) {
        return elasticsearchOperations.save(entity, indexName(entity.getGmtCreate()));
    }

    @Override
    public Boolean saveAll(List<MemberTransactionsDocument> documents) {
        documents.parallelStream().forEach(this::save);
        return Boolean.TRUE;
    }

    @Override
    public List<MemberTransactionsDocument> findByMemberId(long memberId){
        BoolQueryBuilder queryBool = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("memberId",memberId));
        Query query = new NativeSearchQueryBuilder()
                .withQuery(queryBool)
                .build();
        SearchHits<MemberTransactionsDocument> searchHits = elasticsearchOperations.search(query, MemberTransactionsDocument.class);

        return searchHits.stream().map(x-> (MemberTransactionsDocument)x.getContent()).collect(Collectors.toList());
    }
    @Override
    public IPage<MemberWinLossVO> findByDateWinLoss(String username, LocalDateTime start, LocalDateTime end){
        SumAggregationBuilder sumRechargeBuilder = ElasticMemberTransEnum.RECHARGE.getSumBuilder();
        BoolQueryBuilder rangeQuery = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.rangeQuery("gmtCreate")
                        .gte(start)
                        .lte(end));
        FilterAggregationBuilder typeOneBuilder = AggregationBuilders.filter(ElasticMemberTransEnum.RECHARGE.getFilterName(), QueryBuilders.termQuery("type",1)).subAggregation(sumRechargeBuilder);
        DateHistogramAggregationBuilder aggs = AggregationBuilders.dateHistogram("dayGroup")
                .field("gmtCreate").fixedInterval(DateHistogramInterval.days(1)).subAggregation(AggregationBuilders
                        .terms("member_key")
                        .field("memberId")).subAggregation(typeOneBuilder);
        Query query = new NativeSearchQueryBuilder()
                .withQuery(rangeQuery)
                .withAggregations(aggs)
                .build();
        SearchHits<MemberTransactionsDocument> searchHits = elasticsearchOperations.search(query, MemberTransactionsDocument.class);

        return new Page<MemberWinLossVO>().setRecords(Arrays.asList(new MemberWinLossVO())).setPages(1L).setTotal(1).setSize(20L);
    }

    @Override
    public AffiliateWinLossVO findByParentIdWinLoss(Long parentId,LocalDateTime start, LocalDateTime end) {

        String startDT = DateUtil.dateToStr(start,"yyyy-MM-dd'T'HH:mm:ss");
        String endDT = DateUtil.dateToStr(end,"yyyy-MM-dd'T'HH:mm:ss");

        BoolQueryBuilder rangeQuery = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.rangeQuery("gmtCreate")
                        .gte(startDT)
                        .lte(endDT))
                .must(QueryBuilders.termQuery("parentId",parentId));
                //.must(QueryBuilders.termQuery("type",2)).must(QueryBuilders.termQuery("type",6));
        SumAggregationBuilder sumBetBuilder = ElasticMemberTransEnum.BET.getSumBuilder();
        SumAggregationBuilder sumRechargeBuilder = ElasticMemberTransEnum.RECHARGE.getSumBuilder();
        SumAggregationBuilder sumRechargeAwardBuilder = ElasticMemberTransEnum.RECHARGEAWARD.getSumBuilder();
        SumAggregationBuilder sumWithDrawBuilder = ElasticMemberTransEnum.WITHDRAW.getSumBuilder();
        SumAggregationBuilder sumWinLossBuilder = ElasticMemberTransEnum.BET_WINLOSS.getSumBuilder();
        //紅利
        SumAggregationBuilder sumAwardBuilder = ElasticMemberTransEnum.BONUS.getSumBuilder();
        TermsAggregationBuilder termTypeAggregationBuilder = AggregationBuilders
                .terms("project_key")
                .field("type");
        TermsAggregationBuilder termMemberAggregationBuilder = AggregationBuilders
                .terms("member_key")
                .field("memberId");

        FilterAggregationBuilder typeTwoBuilder = AggregationBuilders.filter(ElasticMemberTransEnum.WITHDRAW.getFilterName(),QueryBuilders.termQuery("type",AffMemberTransactionsTypeEnum.WITHDRAW.getCode()))
                .subAggregation(sumWithDrawBuilder);
        FilterAggregationBuilder typeOneBuilder = AggregationBuilders.filter(ElasticMemberTransEnum.RECHARGE.getFilterName(), QueryBuilders.termQuery("type", AffMemberTransactionsTypeEnum.RECHARGE.getCode()))
                .subAggregation(sumRechargeBuilder).subAggregation(sumRechargeAwardBuilder);
        FilterAggregationBuilder typeSixBuilder = AggregationBuilders.filter(ElasticMemberTransEnum.BONUS.getFilterName(), QueryBuilders.termQuery("type",AffMemberTransactionsTypeEnum.BONUS.getCode()))
                .subAggregation(sumAwardBuilder);
        FilterAggregationBuilder typeThirdBuilder = AggregationBuilders.filter(ElasticMemberTransEnum.BET.getFilterName(), QueryBuilders.termQuery("type",AffMemberTransactionsTypeEnum.SETTLE.getCode()))
                .subAggregation(sumBetBuilder).subAggregation(sumWinLossBuilder);
        Query query = new NativeSearchQueryBuilder()
                .withQuery(rangeQuery)
                .withAggregations(termTypeAggregationBuilder.subAggregation(termMemberAggregationBuilder))
                .withAggregations(typeOneBuilder)
                .withAggregations(typeTwoBuilder)
                .withAggregations(typeSixBuilder)
                .withAggregations(typeThirdBuilder)
                .build();

        SearchHits<MemberTransactionsDocument> searchHits = elasticsearchOperations.search(query, MemberTransactionsDocument.class);

        Aggregations aggs = (Aggregations) Objects.requireNonNull(searchHits.getAggregations()).aggregations();
        BigDecimal rechargeAwardSum = Objects.nonNull(getSum.apply(aggs,ElasticMemberTransEnum.RECHARGEAWARD.getFilterInfo())) ? getSum.apply(aggs,ElasticMemberTransEnum.RECHARGEAWARD.getFilterInfo()):BigDecimal.ZERO ;
        BigDecimal rechargeSum = Objects.nonNull(getSum.apply(aggs,ElasticMemberTransEnum.RECHARGE.getFilterInfo())) ? getSum.apply(aggs,ElasticMemberTransEnum.RECHARGE.getFilterInfo()):BigDecimal.ZERO ;
        BigDecimal withdrawSum = Objects.nonNull(getSum.apply(aggs,ElasticMemberTransEnum.WITHDRAW.getFilterInfo())) ? getSum.apply(aggs,ElasticMemberTransEnum.WITHDRAW.getFilterInfo()):BigDecimal.ZERO ;
        BigDecimal awardSum = Objects.nonNull(getSum.apply(aggs,ElasticMemberTransEnum.BONUS.getFilterInfo())) ? getSum.apply(aggs,ElasticMemberTransEnum.BONUS.getFilterInfo()):BigDecimal.ZERO ;
        BigDecimal betSum = Objects.nonNull(getSum.apply(aggs,ElasticMemberTransEnum.BET.getFilterInfo())) ? getSum.apply(aggs,ElasticMemberTransEnum.BET.getFilterInfo()):BigDecimal.ZERO ;
        BigDecimal winLossSum = Objects.nonNull(getSum.apply(aggs,ElasticMemberTransEnum.BET_WINLOSS.getFilterInfo())) ? getSum.apply(aggs,ElasticMemberTransEnum.BET_WINLOSS.getFilterInfo()):BigDecimal.ZERO ;


        //依type count 各類筆數
        Map<String,Long> gNum = getBucketCounts.apply(aggs,"project_key");
        List<? extends Terms.Bucket> topBuckets = ((Terms) aggs.get("project_key")).getBuckets();
        //依type memberId 各類人數
        Map<String,Integer> memberCount = topBuckets.stream()
                .collect(Collectors.toMap(MultiBucketsAggregation.Bucket::getKeyAsString
                        ,y ->{Aggregations as = y.getAggregations();
                            List<? extends Terms.Bucket> bs = ((Terms) as.get("member_key")).getBuckets();
                            return bs.size();
                        }));

        Integer depositRow = Optional.ofNullable(gNum.get("1")).map(x -> Integer.valueOf(x.toString())).orElse(0);
        Integer withdrawRow = Optional.ofNullable(gNum.get("2")).map(x -> Integer.valueOf(x.toString())).orElse(0);;
        Integer depositNum = Optional.ofNullable(memberCount.get("1")).orElse(0);
        Integer withdrawNum = Optional.ofNullable(memberCount.get("2")).orElse(0);;


        //SearchPage<MemberTransactionsDocument>  sp = SearchHitSupport.searchPageFor(searchHits,pageable);
        return AffiliateWinLossVO.builder()
                .bonus(awardSum)
                .fee(BigDecimal.ZERO)
                .agentLevel(1)
                .betAmount(betSum)
                .depositNum(depositNum)
                .depositAmount(rechargeSum)
                .depositAward(rechargeAwardSum)
                .depositRowNum(depositRow)
                .netProfit(winLossSum.negate().subtract(awardSum).subtract(rechargeAwardSum))
                .downPaymentNum(0)
                .username("")
                .periodDepositNum(0)
                .periodDepositAmount(BigDecimal.ZERO)
                .registerNum(0)
                .withdrawAmount(withdrawSum)
                .withdrawNum(withdrawNum)
                .withdrawRowNum(withdrawRow)
                .registerNum(0)
                .rebate(BigDecimal.ZERO)
                .commissionMode("").build();

    }


    BiFunction<Aggregations, String, Map<String, Long>> getBucketCounts = (aggregations, name) -> {
        if (aggregations != null) {
            Aggregation aggNames = aggregations.get(name);
            if (aggNames != null) {
                List<? extends Terms.Bucket> buckets = ((Terms) aggNames).getBuckets();
                if (buckets != null) {
                    return buckets.stream()
                            .collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount));
                }
            }
        }
        return Collections.emptyMap();
    };

    BiFunction<Aggregations, String, Integer> getBucketSize = (aggregations, name) -> {
        if (aggregations != null) {
            Aggregation aggNames = aggregations.get(name);
            if (aggNames != null) {
                List<? extends Terms.Bucket> buckets = ((Terms) aggNames).getBuckets();
                if (buckets != null) {
                    return buckets.size();
                }
            }
        }
        return 0;
    };

    BiFunction<Aggregations,Map<String,String>, BigDecimal> getSum = (aggregations, map)->{
        ParsedFilter pf = aggregations.get(map.get("filter"));
        return BigDecimal.valueOf(((ParsedSum) pf.getAggregations().get(map.get("sum"))).getValue()).setScale(3, RoundingMode.HALF_UP);
    };

    private IndexCoordinates indexName(LocalDateTime dateTime) {
        var indexName = "member-transactions-" + dateTime.format(dateTimeFormatter);
        return IndexCoordinates.of(indexName);
    }
}
