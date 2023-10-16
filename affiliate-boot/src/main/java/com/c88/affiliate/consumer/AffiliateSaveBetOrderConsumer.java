package com.c88.affiliate.consumer;

import com.alibaba.fastjson.JSON;
import com.c88.affiliate.converter.MemberTransactionsDocumentConverter;
import com.c88.affiliate.enums.AffMemberTransactionsTypeEnum;
import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.common.core.constant.TopicConstants;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.game.adapter.enums.BetOrderEventTypeEnum;
import com.c88.game.adapter.event.BetRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class AffiliateSaveBetOrderConsumer {

    private static final String GROUP_ID = "AFFILIATE_MEMBER_BET";

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final MemberTransactionsDocumentConverter memberTransactionsDocumentConverter;

    private final IMemberTransactionsRepository iMemberTransactionsRepository;

    @KafkaListener(id = GROUP_ID, topics = TopicConstants.SAVE_BET_ORDER_EVENT)
    public void listenAffiliateSaveBetOrder(BetRecord betRecord, Acknowledgment acknowledgment) {
        log.info("listenAffiliateSaveBetOrder Consumer: {}", JSON.toJSONString(betRecord));
        try {
            AffiliateMember affiliateMember = Optional.ofNullable(iAffiliateMemberService.findAffiliateMembersByMemberId(betRecord.getMemberId())).orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

            AtomicReference<MemberTransactionsDocument> memberTransactionsDocument = new AtomicReference<>(MemberTransactionsDocument.builder().build());
            iAffMemberTransactionsService
                    .lambdaQuery()
                    .eq(AffMemberTransactions::getGmtCreate, betRecord.getTransactionTime())
                    .eq(AffMemberTransactions::getTradeNo, betRecord.getTransactionNo())
                    .oneOpt()
                    .ifPresentOrElse(transactions -> {
                        if (betRecord.getSettleTime() != null && transactions.getType().equals(AffMemberTransactionsTypeEnum.UN_SETTLE.getCode())) {
                            transactions.setType(AffMemberTransactionsTypeEnum.SETTLE.getCode());
                            transactions.setValidBetAmount(betRecord.getValidBetAmount());
                            transactions.setBetAmount(betRecord.getBetAmount());
                            transactions.setSettle(betRecord.getSettle());
                            transactions.setWinLoss(betRecord.getWinLoss());
                            transactions.setGmtCreate(betRecord.getSettleTime());
                            iAffMemberTransactionsService.saveOrUpdate(transactions);
                            memberTransactionsDocument.set(memberTransactionsDocumentConverter.toDocument(transactions));
                        }
                    }, () -> {
                        AffMemberTransactions affMemberTransactions = new AffMemberTransactions();
                        affMemberTransactions.setMemberId(affiliateMember.getMemberId());
                        affMemberTransactions.setMemberUsername(affiliateMember.getMemberUsername());
                        affMemberTransactions.setMasterId(affiliateMember.getMasterId());
                        affMemberTransactions.setMasterUsername(affiliateMember.getMasterUsername());
                        affMemberTransactions.setParentId(affiliateMember.getParentId());
                        affMemberTransactions.setParentUsername(affiliateMember.getParentUsername());
                        affMemberTransactions.setParents(affiliateMember.getParents());
                        if (Objects.equals(betRecord.getEventType(), BetOrderEventTypeEnum.BET_SETTLED.getValue()) || Objects.equals(betRecord.getEventType(), BetOrderEventTypeEnum.BET_UPDATE_SETTLE.getValue())) {
                            affMemberTransactions.setType(AffMemberTransactionsTypeEnum.SETTLE.getCode());
                        } else if (Objects.equals(betRecord.getEventType(), BetOrderEventTypeEnum.BET_CANCELED.getValue())) {
                            affMemberTransactions.setType(AffMemberTransactionsTypeEnum.CANCEL.getCode());
                        } else if (Objects.equals(betRecord.getEventType(), BetOrderEventTypeEnum.BET_ALREADY_PULL.getValue())) {
                            affMemberTransactions.setType(betRecord.getEventType());
                        } else {
                            affMemberTransactions.setType(AffMemberTransactionsTypeEnum.UN_SETTLE.getCode());
                        }

                        affMemberTransactions.setTradeNo(betRecord.getTransactionNo());
                        affMemberTransactions.setPlatformCode(betRecord.getPlatformCode());
                        affMemberTransactions.setGameCategoryCode(betRecord.getGameCategoryCode());
                        affMemberTransactions.setGameId(betRecord.getGameId());
                        affMemberTransactions.setGameName(betRecord.getGameName());
                        affMemberTransactions.setValidBetAmount(betRecord.getValidBetAmount());
                        affMemberTransactions.setBetAmount(betRecord.getBetAmount());
                        affMemberTransactions.setSettle(betRecord.getSettle());
                        affMemberTransactions.setWinLoss(betRecord.getWinLoss());
                        affMemberTransactions.setTransactionSerial(betRecord.getTransactionSerial());
                        affMemberTransactions.setI18n(betRecord.getGameCategoryCode());
                        iAffMemberTransactionsService.save(affMemberTransactions);
                        memberTransactionsDocument.set(memberTransactionsDocumentConverter.toDocument(affMemberTransactions));
                    });

            // 存入ES
            iMemberTransactionsRepository.save(memberTransactionsDocument.get());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("listenAffiliateMemberRecharge dto:[{}] error:{}", JSON.toJSONString(betRecord), e.getMessage());
        }
    }
}
