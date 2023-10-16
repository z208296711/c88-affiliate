package com.c88.affiliate.consumer;

import com.alibaba.fastjson.JSON;
import com.c88.affiliate.api.dto.AffiliateMemberWithdrawDTO;
import com.c88.affiliate.converter.MemberTransactionsDocumentConverter;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.common.core.constant.TopicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AffiliateMemberWithdrawConsumer {

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private static final String GROUP_ID = "AFFILIATE_MEMBER_WITHDRAW";

    private final MemberTransactionsDocumentConverter memberTransactionsDocumentConverter;

    private final IMemberTransactionsRepository iMemberTransactionsRepository;

    @KafkaListener(id = GROUP_ID, topics = TopicConstants.REMIT)
    @Transactional
    public void affiliateMemberWithdraw(AffiliateMemberWithdrawDTO affiliateMemberDTO, Acknowledgment acknowledgment) {
        log.info("affiliateMemberWithdraw Consumer: {}", JSON.toJSONString(affiliateMemberDTO));
        try {
            AffiliateMember affiliateMember = iAffiliateMemberService.findAffiliateMembersByMemberId(affiliateMemberDTO.getMemberId());

            AffMemberTransactions affMemberTransactions = AffMemberTransactions.builder()
                    .memberId(affiliateMemberDTO.getMemberId())
                    .memberUsername(affiliateMemberDTO.getUsername())
                    .type(AffMemberTransactionsTypeEnum.WITHDRAW.getCode())
                    .withdrawNo(affiliateMemberDTO.getNo())
                    .masterId(affiliateMember.getMasterId())
                    .masterUsername(affiliateMember.getMasterUsername())
                    .parentId(affiliateMember.getParentId())
                    .parentUsername(affiliateMember.getParentUsername())
                    .parents(affiliateMember.getParents())
                    .withdrawAmount(affiliateMemberDTO.getAmount())
                    .note(affiliateMemberDTO.getNote())
                    .build();

            iAffMemberTransactionsService.save(affMemberTransactions);

            // 存入ES
            iMemberTransactionsRepository.save(memberTransactionsDocumentConverter.toDocument(affMemberTransactions));
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("affiliateMemberWithdraw dto:[{}] error:{}", JSON.toJSONString(affiliateMemberDTO), e.getMessage());
        }
    }
}
