package com.c88.affiliate.controller.h5;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.entity.MemberLabel;
import com.c88.affiliate.pojo.form.ModifyAffiliateMemberLabelForm;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IMemberLabelService;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "代理會員標籤")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/member/label")
public class H5MemberLabelController {

    private final IAffiliateMemberService iAffiliateMemberService;

    private final IMemberLabelService iMemberLabelService;

    @Operation(summary = "編輯會員標籤")
    @PutMapping
    public Result<Boolean> modifyAffiliateMemberLabel(@RequestBody ModifyAffiliateMemberLabelForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        Map<String, Long> memberMap = iAffiliateMemberService.lambdaQuery()
                .select(AffiliateMember::getMemberId, AffiliateMember::getMemberUsername)
                .in(AffiliateMember::getMemberUsername, form.getUsernames())
                .list()
                .stream()
                .collect(Collectors.toMap(AffiliateMember::getMemberUsername, AffiliateMember::getMemberId));

        iMemberLabelService.remove(
                Wrappers.<MemberLabel>lambdaQuery().in(MemberLabel::getUsername, form.getUsernames())
        );

        return Result.success(
                iMemberLabelService.saveBatch(
                        form.getUsernames()
                                .stream()
                                .map(username ->
                                        form.getLabelIds()
                                                .stream()
                                                .map(labelId ->
                                                        MemberLabel.builder()
                                                                .memberId(memberMap.get(username))
                                                                .username(username)
                                                                .labelId(labelId)
                                                                .build()
                                                )
                                                .collect(Collectors.toList())
                                )
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                )
        );
    }

}
