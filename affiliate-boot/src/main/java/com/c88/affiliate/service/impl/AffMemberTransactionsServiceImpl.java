package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.form.SearchSwiftAffiliateForm;
import com.c88.affiliate.pojo.vo.AffActiveMembersVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import com.c88.affiliate.pojo.vo.SwiftMemberVO;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.mapper.AffMemberTransactionsMapper;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.result.Result;
import com.c88.common.core.util.DateUtil;
import com.c88.member.api.MemberFeignClient;
import com.c88.member.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AffMemberTransactionsServiceImpl extends ServiceImpl<AffMemberTransactionsMapper, AffMemberTransactions> implements IAffMemberTransactionsService {


    public int countActiveMember(List<Long> agents, String date) {
        LocalDateTime floor = DateUtil.strToFirstDayOfMonthWithLocalDateTime(date);
        LocalDateTime ceiling = floor.plusMonths(1).minusSeconds(1);

        QueryWrapper<AffMemberTransactions> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", agents);
        queryWrapper.ge("gmt_create", floor);
        queryWrapper.lt("gmt_create", ceiling);
        queryWrapper.groupBy("member_id");

        List<AffActiveMembersVO> affActiveMembersVO = agents.size()>0 ? this.baseMapper.countActiveMembers(queryWrapper) : new ArrayList<>();
        Long count = affActiveMembersVO.stream().filter(aff -> aff.getRechargeCount() >= 100 || aff.getValidBetCount() >= 300).count();

        return count.intValue();
    }
}




