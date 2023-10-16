package com.c88.affiliate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.affiliate.converter.AffiliateMemberConverter;
import com.c88.affiliate.mapper.AffMemberTransactionsMapper;
import com.c88.affiliate.mapper.AffiliateMemberMapper;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.SearchAffiliateMemberForm;
import com.c88.affiliate.pojo.form.SearchAffiliateTeamMemberForm;
import com.c88.affiliate.pojo.form.SearchSwiftAffiliateForm;
import com.c88.affiliate.pojo.form.SearchSwiftAffiliateＭemberForm;
import com.c88.affiliate.pojo.vo.AffiliateMemberSubVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import com.c88.affiliate.pojo.vo.H5AffiliateTeamMemberVO;
import com.c88.affiliate.pojo.vo.SwiftMemberVO;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.common.core.result.Result;
import com.c88.member.api.MemberFeignClient;
import com.c88.member.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliateMemberServiceImpl extends ServiceImpl<AffiliateMemberMapper, AffiliateMember> implements IAffiliateMemberService {

    private final AffiliateMemberConverter affiliateMemberConverter;

    private final AffMemberTransactionsMapper affMemberTransactionsMapper;

    private final MemberFeignClient memberFeignClient;

    @Override
    public IPage<SwiftMemberVO> findSwiftMembersPage(SearchSwiftAffiliateＭemberForm form) {

        SearchSwiftAffiliateForm searchSwiftAffiliateForm = new SearchSwiftAffiliateForm();
        BeanUtils.copyProperties(form, searchSwiftAffiliateForm);
        searchSwiftAffiliateForm.setStartTime(LocalDateTime.of(form.getStartTime(), LocalTime.MIN));
        searchSwiftAffiliateForm.setEndTime(LocalDateTime.of(form.getEndTime(), LocalTime.MAX));

        //查找對應玩家所有資訊
        IPage<SwiftMemberVO> swiftMemberVOMap = affMemberTransactionsMapper.findSwiftMembersTransaction(new Page<>(form.getPageNum(), form.getPageSize()), searchSwiftAffiliateForm);

        Map<Long, MemberInfoDTO> memberInfoDTOMap = new HashMap<>();
        Result<List<MemberInfoDTO>> memberInfoByUsernamesResult = memberFeignClient.getMemberInfoByUsernames(swiftMemberVOMap.getRecords().stream().map(SwiftMemberVO::getMemberUsername).collect(Collectors.toList()));
        if (Result.isSuccess(memberInfoByUsernamesResult)) {
            memberInfoDTOMap = memberInfoByUsernamesResult.getData().stream().collect(Collectors.toMap(MemberInfoDTO::getId, Function.identity()));
        }
        Map<Long, MemberInfoDTO> finalMemberInfoDTOMap = memberInfoDTOMap;
        swiftMemberVOMap.getRecords().forEach(x -> {
            if (!finalMemberInfoDTOMap.containsKey(x.getMemberId())) {
                return;
            }
            MemberInfoDTO memberInfoDTO = finalMemberInfoDTOMap.getOrDefault(x.getMemberId(), null);
            if (memberInfoDTO != null) {
                x.setLastLoginTime(memberInfoDTO.getLastLoginTime());
                x.setRegisterTime(memberInfoDTO.getRegisterTime());
            }
        });
        swiftMemberVOMap.setTotal(swiftMemberVOMap.getTotal() / 3);
        return swiftMemberVOMap;
    }

    @Override
    public IPage<H5AffiliateMemberVO> findSubordinateMembers(Long affiliateId, SearchAffiliateMemberForm form) {
        IPage<AffiliateMember> h5AffiliateMemberVOIPage = this.lambdaQuery()
                .eq(AffiliateMember::getParentId, affiliateId)
                .eq(AffiliateMember::getMemberUsername, form.getUsername())
                .page(new Page<>(form.getPageNum(), form.getPageSize()));
        return null;
    }

    @Override
    public IPage<H5AffiliateTeamMemberVO> findTeamMembers(Long affiliateId, SearchAffiliateTeamMemberForm form) {
        IPage<AffiliateMember> h5AffiliateMemberVOIPage = this.lambdaQuery()
                .eq(AffiliateMember::getParentId, affiliateId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()));


        return null;
    }

    @Override
    public List<AffiliateMemberDTO> findAffiliateMembers(List<Long> memberIds) {
        return this.lambdaQuery()
                .in(AffiliateMember::getMemberId, memberIds)
                .list()
                .stream()
                .map(affiliateMemberConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AffiliateMemberDTO> findAffiliateMembersByParentUsername(String parentUsername) {
        return this.lambdaQuery()
                .eq(AffiliateMember::getParentUsername, parentUsername)
                .or()
                .eq(AffiliateMember::getMasterUsername, parentUsername)
                .list()
                .stream()
                .map(affiliateMemberConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "affiliate", key = "'member:'+#memberId", unless = "#result == null")
    public AffiliateMember findAffiliateMembersByMemberId(Long memberId) {
        return this.lambdaQuery()
                .eq(AffiliateMember::getMemberId, memberId)
                .one();
    }

    @Override
    public List<AffiliateMemberSubVO> findMemberByParentId(List<Long> parentIds) {
        return this.getBaseMapper().findMemberByParentId(parentIds);
    }

    @Override
    public IPage<SwiftMemberVO> findSwiftMember(SearchSwiftAffiliateForm form) {
        //todo


        return new Page<>(form.getPageNum(), form.getPageSize());
    }

    @Override
    public AffiliateMember findAffiliateIdByMember(String username) {
        return this.lambdaQuery()
                .eq(AffiliateMember::getMemberUsername, username)
                .one();
    }

    @Override
    public List<AffiliateMember> getMembersOfSubAgent(Long parentId) {
        return this.lambdaQuery().likeRight(AffiliateMember::getParents, parentId).list();
    }


}




