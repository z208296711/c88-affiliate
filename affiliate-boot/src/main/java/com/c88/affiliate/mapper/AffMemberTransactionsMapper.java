package com.c88.affiliate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.form.*;
import com.c88.affiliate.pojo.vo.*;
import com.c88.affiliate.pojo.form.SearchSwiftAffiliateForm;
import com.c88.affiliate.pojo.vo.AffiliateMemberInfoVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import com.c88.affiliate.pojo.vo.H5AffiliateTeamMemberVO;
import com.c88.affiliate.pojo.vo.H5MemberPlayingRecordVO;
import com.c88.affiliate.pojo.vo.SwiftMemberVO;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.c88.affiliate.pojo.entity.AffMemberTransactions
 */
public interface AffMemberTransactionsMapper extends BaseMapper<AffMemberTransactions> {

    IPage<H5AffiliateMemberVO> findAffiliateMembersTransaction(Page page, SearchAffiliateMemberForm form, Long affiliateId);

    IPage<H5AffiliateTeamMemberVO> findAffiliateTeamMembersTransaction(Page page, SearchAffiliateTeamMemberForm form, Long affiliateId);

    IPage<SwiftMemberVO> findSwiftMembersTransaction(Page<SwiftMemberVO> page, SearchSwiftAffiliateForm form);

//    @MapKey("memberId")
//    Map<Long,SwiftMemberVO> findSwiftMembersTransaction(SearchSwiftAffiliateForm form);

    IPage<H5MemberPlayingRecordVO> findMemberPlayRecordWithoutStatus(Page page, SearchMemberPlayingRecordForm form);

    IPage<H5MemberPlayingRecordVO> findMemberPlayRecord(Page page, SearchMemberPlayingRecordForm form);

    @Select("SELECT sum(win_loss) as win_loss, sum(recharge_award_amount) as recharge_award_amount, sum(award_amount)as award_amount" +
            ", sum(platform_fee) as platform_fee from aff_member_transactions ${ew.customSqlSegment}")
    AffTransactionCountVO countAffTransaction(@Param(Constants.WRAPPER) QueryWrapper<AffTransactionCountVO> queryWrapper);

    @Select("SELECT member_id, sum(recharge_amount) rechargeCount, sum(valid_bet_amount) validBetCount  from aff_member_transactions ${ew.customSqlSegment}")
    List<AffActiveMembersVO> countActiveMembers(@Param(Constants.WRAPPER) QueryWrapper<AffMemberTransactions> queryWrapper);

    AffiliateMemberInfoVO findAffiliateMemberInfo(FindAffiliateMemberInfoForm form);

    AffiliateMemberInfoVO findAffiliateMemberInfo(String username, LocalDateTime startTime, LocalDateTime endTime);


    @Select("SELECT platform_code,sum(win_loss) win_loss, sum(platform_fee) platform_fee  from aff_member_transactions " +
            " ${ew.customSqlSegment}")
    List<AffiliatePlatformDetailVO> getPlatformDetailReport(@Param(Constants.WRAPPER) QueryWrapper<AffiliatePlatformDetailVO> queryWrapper);

    IPage<AffTransactionCountVO> sumAffTransactionByPage(Page page, SearchAffiliateTeamTransForm form);

}




