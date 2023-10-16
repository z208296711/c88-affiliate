package com.c88.affiliate.consumer;

import com.alibaba.fastjson.JSON;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.constant.TopicConstants;
import com.c88.member.dto.MemberRegisterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AffiliateMemberRegisterConsumer {

    private final IAffiliateService iAffiliateService;

    private final IAffiliateMemberService iAffiliateMemberService;

    private static final Long DEFAULT_AFFILIATE_ID = 1L;

    private static final String GROUP_ID = "AFFILIATE_REGISTER";

    @KafkaListener(id = GROUP_ID, topics = TopicConstants.MEMBER_REGISTER)
    public void listenAffiliateRegister(MemberRegisterDTO affiliateMemberDTO, Acknowledgment acknowledgement) {
        log.info("listenAffiliateRegister Consumer: {}", JSON.toJSONString(affiliateMemberDTO));
        try {
            AffiliateMember affiliateMember = new AffiliateMember();
            affiliateMember.setRegisterTime(affiliateMemberDTO.getGmtCreate());
            if (StringUtils.isBlank(affiliateMemberDTO.getPromotionCode())) {
                Affiliate masterAffiliate = iAffiliateService.getById(DEFAULT_AFFILIATE_ID);
                affiliateMember.setMasterId(masterAffiliate.getMasterId());
                affiliateMember.setMasterUsername(masterAffiliate.getMasterUsername());
                affiliateMember.setParentId(masterAffiliate.getId());
                affiliateMember.setParentUsername(masterAffiliate.getUsername());
                affiliateMember.setMemberId(affiliateMemberDTO.getId());
                affiliateMember.setMemberUsername(affiliateMemberDTO.getUsername());
                affiliateMember.setParents(masterAffiliate.getParents());
            } else {
                iAffiliateService.lambdaQuery()
                        .eq(Affiliate::getPromotionCode, affiliateMemberDTO.getPromotionCode())
                        .oneOpt()
                        .ifPresentOrElse(affiliate -> {
                            affiliateMember.setMasterId(affiliate.getMasterId());
                            affiliateMember.setMasterUsername(affiliate.getMasterUsername());
                            affiliateMember.setParentId(affiliate.getId());
                            affiliateMember.setParentUsername(affiliate.getUsername());
                            affiliateMember.setMemberId(affiliateMemberDTO.getId());
                            affiliateMember.setMemberUsername(affiliateMemberDTO.getUsername());
                            affiliateMember.setParents(affiliate.getParents());
                        }, () -> {
                            Affiliate masterAffiliate = iAffiliateService.getById(DEFAULT_AFFILIATE_ID);
                            affiliateMember.setMasterId(masterAffiliate.getMasterId());
                            affiliateMember.setMasterUsername(masterAffiliate.getMasterUsername());
                            affiliateMember.setParentId(masterAffiliate.getId());
                            affiliateMember.setParentUsername(masterAffiliate.getUsername());
                            affiliateMember.setMemberId(affiliateMemberDTO.getId());
                            affiliateMember.setMemberUsername(affiliateMemberDTO.getUsername());
                            affiliateMember.setParents(masterAffiliate.getParents());
                        });
            }

            iAffiliateMemberService.save(affiliateMember);
            acknowledgement.acknowledge();

        } catch (Exception e) {
            log.error("listenAffiliateRegister dto:[{}] error:[{}]", JSON.toJSONString(affiliateMemberDTO), e);
        }
    }
}
