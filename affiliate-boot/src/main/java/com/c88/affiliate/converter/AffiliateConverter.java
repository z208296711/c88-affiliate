package com.c88.affiliate.converter;

import com.c88.affiliate.api.dto.AffiliateInfoDTO;
import com.c88.affiliate.api.dto.AuthAffiliateDTO;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.form.AffiliateForm;
import com.c88.affiliate.pojo.vo.AdminAffiliateVO;
import com.c88.affiliate.pojo.vo.AffiliateMemberSubVO;
import com.c88.affiliate.pojo.vo.H5AffiliateVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AffiliateConverter {

    H5AffiliateVO toH5AffiliateVO(Affiliate entity);

    Affiliate toEntity(AffiliateForm affiliateForm);

    AuthAffiliateDTO toDTO(Affiliate affiliate);

    AffiliateInfoDTO toInfoDTO(Affiliate affiliate);

    AdminAffiliateVO toAdminAffiliateVO(Affiliate affiliate);

    default AdminAffiliateVO toAdminAffiliateVO(Affiliate affiliate,
                                                Map<Long, String> commissionGroupMap,
                                                Map<Long, Integer> affiliateMemberSubVOMap,
                                                Map<Long, BigDecimal> balanceMap,
                                                String agentUrl) {
        AdminAffiliateVO adminAffiliateVO = this.toAdminAffiliateVO(affiliate);
        adminAffiliateVO.setCommissionGroup(commissionGroupMap.getOrDefault(affiliate.getCommissionGroupId(), ""));
        adminAffiliateVO.setParentUsername(affiliate.getUsername().equals(affiliate.getParentUsername()) ? "" : affiliate.getParentUsername());
        adminAffiliateVO.setSubordinateCount(affiliateMemberSubVOMap.getOrDefault(affiliate.getId(), 0));
        adminAffiliateVO.setBalance(balanceMap.getOrDefault(affiliate.getId(), BigDecimal.ZERO));
        String longLink = agentUrl + "/register?code=" + affiliate.getPromotionCode();
        adminAffiliateVO.setLink(longLink);
        return adminAffiliateVO;
    }
}
