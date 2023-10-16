package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.vo.AffiliateCommissionTeamReportVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AffMemberTransactionsConverter {

    H5AffiliateMemberVO toVO(AffMemberTransactions entity);

    AffiliateCommissionTeamReportVO toTeamCommission(AffAffiliateCommissionRecord entity);

}
