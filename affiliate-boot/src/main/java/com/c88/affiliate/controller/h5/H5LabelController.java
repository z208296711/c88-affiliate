package com.c88.affiliate.controller.h5;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.c88.affiliate.converter.LabelConverter;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.entity.Label;
import com.c88.affiliate.pojo.entity.MemberLabel;
import com.c88.affiliate.pojo.form.AddAffiliateLabelForm;
import com.c88.affiliate.pojo.form.ModifyAffiliateLabelForm;
import com.c88.affiliate.pojo.vo.LabelMemberManagementVO;
import com.c88.affiliate.pojo.vo.LabelVO;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.ILabelService;
import com.c88.affiliate.service.IMemberLabelService;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "代理會員標籤")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/label")
public class H5LabelController {

    private final IMemberLabelService iMemberLabelService;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final ILabelService iLabelService;

    private final LabelConverter labelConverter;

    @Operation(summary = "找代理標籤")
    @GetMapping
    public Result<List<LabelVO>> findAffiliateLabel() {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        List<MemberLabel> memberLabels = iMemberLabelService.list();

        return Result.success(
                iLabelService.lambdaQuery()
                        .eq(Label::getParentId, affiliateId)
                        .list()
                        .stream()
                        .map(label -> {
                                    LabelVO labelVO = labelConverter.toVO(label);
                                    labelVO.setMemberQuantity(memberLabels.stream().filter(filter -> Objects.equals(filter.getLabelId(), label.getId())).count());
                                    return labelVO;
                                }
                        )
                        .collect(Collectors.toList())
        );
    }

    @Operation(summary = "找代理標籤", description = "會員管理")
    @GetMapping("/member/management")
    public Result<List<LabelMemberManagementVO>> findAffiliateLabelMemberManagement() {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        // 取得代理的標籤
        List<Label> labels = iLabelService.lambdaQuery()
                .eq(Label::getParentId, affiliateId)
                .list();

        List<LabelMemberManagementVO> labelMemberManagements = new java.util.ArrayList<>();

        // 有標籤的會員數量
        int memberLabelCount = 0;
        if (CollectionUtils.isNotEmpty(labels)) {
            List<MemberLabel> memberLabels = iMemberLabelService.lambdaQuery()
                    .in(MemberLabel::getLabelId, labels.stream().map(Label::getId).collect(Collectors.toList()))
                    .list();

            memberLabelCount = memberLabels.stream().map(MemberLabel::getMemberId).collect(Collectors.toSet()).size();

            labelMemberManagements.addAll(
                    labels.stream()
                            .map(label ->
                                    LabelMemberManagementVO.builder()
                                            .labelId(label.getId())
                                            .labelName(label.getName())
                                            .labelCount(memberLabels.stream().filter(filter -> Objects.equals(filter.getLabelId(), label.getId())).count())
                                            .build()
                            )
                            .collect(Collectors.toList())
            );
        }

        // 取得下級會員數量
        Integer memberCount = iAffiliateMemberService.lambdaQuery()
                .eq(AffiliateMember::getParentId, affiliateId)
                .count();

        // 未使用標籤的會員
        labelMemberManagements.add(
                LabelMemberManagementVO.builder()
                        .labelId(-1L)
                        .labelName("unused")
                        .labelCount(
                                (long) (memberCount - memberLabelCount)
                        )
                        .build()
        );

        // 加上全部會員的數量
        labelMemberManagements.add(
                LabelMemberManagementVO.builder()
                        .labelId(0L)
                        .labelName("all")
                        .labelCount(Long.valueOf(memberCount))
                        .build()
        );

        return Result.success(labelMemberManagements);
    }

    @Operation(summary = "新增代理標籤")
    @PostMapping
    public Result<Boolean> addAffiliateLabel(@RequestBody AddAffiliateLabelForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        // 判斷代理標籤名稱不得重複且上限為20組
        if (iLabelService.lambdaQuery().eq(Label::getParentId, affiliateId).count() >= 20) {
            throw new BizException(ResultCode.AFFILIATE_MEMBER_LABEL_LIMIT_EXCEEDED);
        }

        iLabelService.lambdaQuery()
                .eq(Label::getParentId, affiliateId)
                .eq(Label::getName, form.getName())
                .oneOpt()
                .ifPresent(x -> {
                            throw new BizException(ResultCode.AFFILIATE_MEMBER_LABEL_NAME_EXIST);
                        }
                );

        return Result.success(iLabelService.save(Label.builder().parentId(affiliateId).name(form.getName()).build()));
    }

    @Operation(summary = "編輯代理標籤")
    @PutMapping
    public Result<Boolean> modifyAffiliateLabel(@Validated @RequestBody ModifyAffiliateLabelForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        // 判斷代理標籤名稱不得重複
        iLabelService.lambdaQuery()
                .eq(Label::getParentId, affiliateId)
                .eq(Label::getName, form.getName())
                .oneOpt()
                .ifPresent(x -> {
                            throw new BizException(ResultCode.AFFILIATE_MEMBER_LABEL_NAME_EXIST);
                        }
                );

        return Result.success(iLabelService.updateById(Label.builder().id(form.getId()).name(form.getName()).build()));
    }

    @Transactional
    @Operation(summary = "刪除代理標籤")
    @DeleteMapping
    public Result<Boolean> deleteAffiliateLabel(@RequestBody List<Long> ids) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        iMemberLabelService.remove(Wrappers.<MemberLabel>lambdaQuery().in(MemberLabel::getLabelId, ids));

        return Result.success(iLabelService.removeByIds(ids));
    }

}
