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
import com.c88.payment.dto.MemberRechargeSuccessDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AffiliateMemberRechargeConsumer {

    private static final String GROUP_ID = "AFFILIATE_MEMBER_RECHARGE";

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final MemberTransactionsDocumentConverter memberTransactionsDocumentConverter;

    private final IMemberTransactionsRepository iMemberTransactionsRepository;

    @KafkaListener(id = GROUP_ID, topics = TopicConstants.RECHARGE_SUCCESS)
    public void listenAffiliateMemberRecharge(MemberRechargeSuccessDTO memberRechargeSuccessDTO, Acknowledgment acknowledgment) {

        log.info("listenAffiliateMemberRecharge Consumer: {}", JSON.toJSONString(memberRechargeSuccessDTO));
        try {
            AffiliateMember affiliateMember = Optional.ofNullable(iAffiliateMemberService.findAffiliateMembersByMemberId(memberRechargeSuccessDTO.getMemberId()))
                    .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

            AffMemberTransactions affMemberTransactions = new AffMemberTransactions();
            affMemberTransactions.setMemberId(affiliateMember.getMemberId());
            affMemberTransactions.setMemberUsername(affiliateMember.getMemberUsername());
            affMemberTransactions.setMasterId(affiliateMember.getMasterId());
            affMemberTransactions.setMasterUsername(affiliateMember.getMasterUsername());
            affMemberTransactions.setParentId(affiliateMember.getParentId());
            affMemberTransactions.setParentUsername(affiliateMember.getParentUsername());
            affMemberTransactions.setParents(affiliateMember.getParents());
            affMemberTransactions.setType(AffMemberTransactionsTypeEnum.RECHARGE.getCode());
            affMemberTransactions.setTradeNo(memberRechargeSuccessDTO.getTradeNo());
            affMemberTransactions.setPayType(String.valueOf(memberRechargeSuccessDTO.getRechargeTypeId()));
            affMemberTransactions.setRechargeAwardAmount(memberRechargeSuccessDTO.getRechargeAwardAmount());
            affMemberTransactions.setRechargeAmount(memberRechargeSuccessDTO.getAmount());
            affMemberTransactions.setRealAmount(memberRechargeSuccessDTO.getRealAmount());
            affMemberTransactions.setI18n(memberRechargeSuccessDTO.getRechargeTypeI18nKey());
            affMemberTransactions.setNote(memberRechargeSuccessDTO.getNotes());
            iAffMemberTransactionsService.save(affMemberTransactions);

            // 存入ES
            iMemberTransactionsRepository.save(memberTransactionsDocumentConverter.toDocument(affMemberTransactions));
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("listenAffiliateMemberRecharge dto:[{}] error:{}", JSON.toJSONString(memberRechargeSuccessDTO), e.getMessage());
        }
    }
}
