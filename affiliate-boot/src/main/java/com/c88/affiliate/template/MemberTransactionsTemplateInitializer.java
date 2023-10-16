package com.c88.affiliate.template;

import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberTransactionsTemplateInitializer {

    private static final String TEMPLATE_NAME = "member-transactions-template";

    private static final String TEMPLATE_PATTERN = "member-transactions-*";

    private final ElasticsearchOperations operations;

    @PostConstruct
    public void init() {
        try {
            IndexOperations indexOps = operations.indexOps(MemberTransactionsDocument.class);
            if (!indexOps.existsTemplate(TEMPLATE_NAME)) {
                Document mapping = indexOps.createMapping();
                AliasActions aliasActions = new AliasActions().add(
                        new AliasAction.Add(AliasActionParameters.builderForTemplate()
                                .withAliases(indexOps.getIndexCoordinates().getIndexNames())
                                .build())
                );
                PutTemplateRequest request = PutTemplateRequest.builder(TEMPLATE_NAME, TEMPLATE_PATTERN)
                        .withMappings(mapping)
                        .withAliasActions(aliasActions)
                        .build();
                indexOps.putTemplate(request);
            }
        } catch (Exception e) {
            log.error("MemberTransactionsTemplateInitializer : {}", e);
        }
    }
}
