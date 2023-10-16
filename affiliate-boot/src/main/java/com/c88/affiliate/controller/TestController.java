package com.c88.affiliate.controller;

import com.c88.affiliate.converter.MemberTransactionsDocumentConverter;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Profile("!prod & !pre")
@Tag(name = "測試各項功能")
public class TestController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;
    private final IAffiliateMemberService iAffiliateMemberService;
    private final IMemberTransactionsRepository iMemberTransactionsRepository;
    private final MemberTransactionsDocumentConverter memberTransactionsDocumentConverter;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Operation(summary = "ES增加測試Transactions")
    @GetMapping("/add/Document")
    public Result<List<AffMemberTransactions>> ddd() {
        AffiliateMember affiliateMember = Optional.ofNullable(iAffiliateMemberService.findAffiliateMembersByMemberId(116L))
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        AffMemberTransactions affMemberTransactions = new AffMemberTransactions();
        affMemberTransactions.setMemberId(affiliateMember.getMemberId());
        affMemberTransactions.setMemberUsername(affiliateMember.getMemberUsername());
        affMemberTransactions.setMasterId(affiliateMember.getMasterId());
        affMemberTransactions.setMasterUsername(affiliateMember.getMasterUsername());
        affMemberTransactions.setParentId(affiliateMember.getParentId());
        affMemberTransactions.setParentUsername(affiliateMember.getParentUsername());
        affMemberTransactions.setParents(affiliateMember.getParents());
        affMemberTransactions.setType(AffMemberTransactionsTypeEnum.BONUS.getCode());
        affMemberTransactions.setAwardAmount(BigDecimal.ONE);
        affMemberTransactions.setGmtCreate(LocalDateTime.now().minusMonths(1));
        affMemberTransactions.setI18n("memberdetails.accountchange_column04_03_08");
        iAffMemberTransactionsService.save(affMemberTransactions);

        // 存入ES
        iMemberTransactionsRepository.save(memberTransactionsDocumentConverter.toDocument(affMemberTransactions));

        return Result.success(null);
    }

    @Operation(summary = "ES搜尋Transactions")
    @GetMapping("/search")
    public Result<List<AffMemberTransactions>> yyy() {

        QueryBuilder queryBuilderCh =
                new QueryStringQueryBuilder("1")
                        .field("id");

        Query query = new NativeSearchQueryBuilder()
                .withFilter(queryBuilderCh)
                .build();

        SearchHits<MemberTransactionsDocument> search = elasticsearchRestTemplate.search(query, MemberTransactionsDocument.class);
        log.info("search hit info : {}", search);
        search.forEach(x -> log.info("document info : {}", x.getContent()));
        return Result.success(null);
    }

    @Operation(summary = "reset")
    @GetMapping("/reset")
    public Result<List<AffMemberTransactions>> reset() {

        List<AffMemberTransactions> list = iAffMemberTransactionsService.list();


        iMemberTransactionsRepository.saveAll(memberTransactionsDocumentConverter.toDocument(list));

        return Result.success(null);
    }


}
