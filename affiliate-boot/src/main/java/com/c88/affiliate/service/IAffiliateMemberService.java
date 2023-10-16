package com.c88.affiliate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.affiliate.pojo.form.*;
import com.c88.affiliate.pojo.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public interface IAffiliateMemberService extends IService<AffiliateMember> {

    IPage<SwiftMemberVO> findSwiftMembersPage(SearchSwiftAffiliateＭemberForm form);

    IPage<H5AffiliateMemberVO> findSubordinateMembers(Long affiliateId, SearchAffiliateMemberForm form);

    IPage<H5AffiliateTeamMemberVO> findTeamMembers(Long affiliateId, SearchAffiliateTeamMemberForm form);

    List<AffiliateMemberDTO> findAffiliateMembers(List<Long> memberIds);

    List<AffiliateMemberDTO> findAffiliateMembersByParentUsername(String parentUsername);

    AffiliateMember findAffiliateMembersByMemberId(Long memberId);

    List<AffiliateMemberSubVO> findMemberByParentId(List<Long> parentIds);

    IPage<SwiftMemberVO> findSwiftMember(SearchSwiftAffiliateForm form);

    AffiliateMember findAffiliateIdByMember(String username);

    List<AffiliateMember> getMembersOfSubAgent(Long parentId);

}
