package com.c88.affiliate.consumer;

import com.c88.affiliate.enums.AffiliateOperationTypeEnum;
import com.c88.affiliate.pojo.entity.AffiliateOperationLog;
import com.c88.affiliate.service.IAffiliateOperationLogService;
import com.c88.common.core.constant.TopicConstants;
import com.c88.payment.dto.AddAffiliateBalanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AffiliateOperationLogConsumer {

    private final IAffiliateOperationLogService iAffiliateOperationLogService;

    @KafkaListener(id = "affiliate-operation-log", topics = TopicConstants.AFFILIATE_OPERATION_LOG)
    public void listenAffiliateOperationLog(AddAffiliateBalanceDTO affiliateBalanceDTO, Acknowledgment acknowledgment) {
        //新增異動紀錄
        AffiliateOperationLog operationLog = new AffiliateOperationLog();
        operationLog.setType(AffiliateOperationTypeEnum.EDIT_ADMIN_BALANCE.getValue());
        operationLog.setAffiliateId(affiliateBalanceDTO.getAffiliateId());
        operationLog.setAffiliateUsername(affiliateBalanceDTO.getUserName());
        operationLog.setContent(String.valueOf(affiliateBalanceDTO.getAmount()));
        operationLog.setUpdateBy(affiliateBalanceDTO.getUpdateUser());
        iAffiliateOperationLogService.save(operationLog);
        acknowledgment.acknowledge();
    }
}
