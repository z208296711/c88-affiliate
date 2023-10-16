package com.c88.affiliate.consumer;

import com.alibaba.fastjson.JSON;
import com.c88.affiliate.converter.MemberTransactionsDocumentConverter;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.payment.dto.RechargeBonusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AffiliateMemberBonusConsumer {

    private static final String GROUP_ID = "AFFILIATE_MEMBER_BONUS";

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final MemberTransactionsDocumentConverter memberTransactionsDocumentConverter;

    private final IMemberTransactionsRepository iMemberTransactionsRepository;

    @KafkaListener(id = GROUP_ID, topics = TopicConstants.RECHARGE_BONUS)
    public void listenAffiliateMemberRechargeBonus(RechargeBonusDTO rechargeBonusDTO, Acknowledgment acknowledgment) {

        log.info("listenAffiliateMemberRechargeBonus Consumer: {}", JSON.toJSONString(rechargeBonusDTO));
        try {
            AffiliateMember affiliateMember = Optional.ofNullable(iAffiliateMemberService.findAffiliateMembersByMemberId(rechargeBonusDTO.getMemberId()))
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
            affMemberTransactions.setAwardAmount(rechargeBonusDTO.getAmount());
            affMemberTransactions.setI18n(rechargeBonusDTO.getBalanceChangeTypeLinkEnum().getI18n());
            iAffMemberTransactionsService.save(affMemberTransactions);

            // 存入ES
            iMemberTransactionsRepository.save(memberTransactionsDocumentConverter.toDocument(affMemberTransactions));
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("listenAffiliateMemberRechargeBonus dto:[{}] error:{}", JSON.toJSONString(rechargeBonusDTO), e.getMessage());
        }
    }
}
