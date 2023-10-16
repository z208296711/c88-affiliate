package com.c88.affiliate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.SearchAffiliateMemberForm;
import com.c88.affiliate.pojo.form.SearchAffiliateTeamMemberForm;
import com.c88.affiliate.pojo.vo.AffiliateMemberSubVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import com.c88.affiliate.pojo.vo.H5AffiliateTeamMemberVO;

import java.util.List;

/**
 * @Entity com.c88.affiliate.pojo.entity.AffiliateMember
 */
public interface AffiliateMemberMapper extends BaseMapper<AffiliateMember> {

    List<AffiliateMemberSubVO> findMemberByParentId(List<Long> parentIds);

    List<H5AffiliateMemberVO> findAffiliateMembers(SearchAffiliateMemberForm form, Long affiliateId);

    List<H5AffiliateTeamMemberVO> findAffiliateTeamMembersTransaction(SearchAffiliateTeamMemberForm form, Long affiliateId);
}




