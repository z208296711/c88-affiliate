package com.c88.affiliate.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.affiliate.pojo.form.*;
import com.c88.affiliate.pojo.vo.AffiliateWinLossVO;
import com.c88.affiliate.pojo.vo.MemberWinLossVO;
import com.c88.affiliate.pojo.vo.SwiftMemberVO;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateMemberWinLossService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.payment.vo.MemberDepositDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "『後台』代理會員關聯")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/affiliate/member")
public class AffiliateMemberController {

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IAffiliateService iAffiliateService;

    private final IAffiliateMemberWinLossService iAffiliateMemberWinLossService;

    @Operation(summary = "找會員的代理關聯")
    @GetMapping
    public Result<List<AffiliateMemberDTO>> findAffiliateMembers(@RequestParam List<Long> memberIds) {
        return Result.success(iAffiliateMemberService.findAffiliateMembers(memberIds));
    }

    @Operation(summary = "找會員的代理關聯", description = "代理帳號")
    @GetMapping("/parent/username")
    public Result<List<AffiliateMemberDTO>> findAffiliateMembersByParentUsername(@RequestParam String parentUsername) {
        return Result.success(iAffiliateMemberService.findAffiliateMembersByParentUsername(parentUsername));
    }

    @Operation(summary = "查詢-玩家轉移", description = "玩家轉移")
    @GetMapping("/swift")
    public PageResult<SwiftMemberVO> findSwiftMembers(@ParameterObject SearchSwiftAffiliateＭemberForm form) {
        return PageResult.success(iAffiliateMemberService.findSwiftMembersPage(form));
    }

    @Operation(summary = "操作-玩家轉移", description = "玩家轉移")
    @PostMapping("/swift")
    public Result<Boolean> doSwiftMembers(@RequestBody SwiftAffiliateForm form) {
        return Result.success(iAffiliateService.doSwiftMember(form));
    }

    @Operation(summary = "代理輸贏報表", description = "查詢代理輸贏報表")
    @GetMapping("/affiliateWinLoss")
    public PageResult<AffiliateWinLossVO> getAffiliateWinLossRecord(@ParameterObject SearchAffiliateWinLossForm form){
        return PageResult.success(iAffiliateMemberWinLossService.getAffiliateWinLoss(form));
    }

}
