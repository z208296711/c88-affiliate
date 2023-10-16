package com.c88.affiliate.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.c88.affiliate.converter.CommissionGroupConverter;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.CommissionGroup;
import com.c88.affiliate.pojo.form.AddCommissionGroupForm;
import com.c88.affiliate.pojo.form.AddOrModifyCommissionGroupForm;
import com.c88.affiliate.pojo.form.ModifyCommissionGroupForm;
import com.c88.affiliate.pojo.form.TransferAffCommissionGroupForm;
import com.c88.affiliate.pojo.vo.*;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.affiliate.service.ICommissionGroupService;
import com.c88.common.core.base.BasePageQuery;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.member.vo.OptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "佣金群組")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/affiliate/commission/group")
public class CommissionGroupController {

    private final IAffiliateService iAffiliateService;

    private final ICommissionGroupService iAffCommissionGroupService;

    private final CommissionGroupConverter affiliateCommissionGroupConverter;

    @Operation(summary = "找佣金群組")
    @GetMapping
    public PageResult<CommissionGroupVO> findAffCommissionGroup(@ParameterObject BasePageQuery form) {
        IPage<CommissionGroupVO> affCommissionGroupCombinationPage = iAffCommissionGroupService.lambdaQuery()
                .orderByDesc(CommissionGroup::getId)
                .page(new Page<>(form.getPageNum(), form.getPageSize()))
                .convert(commissionGroup -> {
                            CommissionGroupVO affCommissionGroupVO = affiliateCommissionGroupConverter.toVO(commissionGroup);

                            if (Objects.nonNull(affCommissionGroupVO.getDetails())) {
                                affCommissionGroupVO.setActivityMembers(affCommissionGroupVO.getDetails().stream().map(x -> ActivityMemberVO.builder().level(x.getLevel()).value(x.getActivityMember()).build()).collect(Collectors.toList()));
                                affCommissionGroupVO.setMinProfits(affCommissionGroupVO.getDetails().stream().map(x -> MinProfitVO.builder().level(x.getLevel()).value(x.getMinProfit()).build()).collect(Collectors.toList()));
                                affCommissionGroupVO.setMaxProfits(affCommissionGroupVO.getDetails().stream().map(x -> MaxProfitVO.builder().level(x.getLevel()).value(x.getMaxProfit()).build()).collect(Collectors.toList()));
                                affCommissionGroupVO.setRates(affCommissionGroupVO.getDetails().stream().map(x -> CommissionRateVO.builder().level(x.getLevel()).value(x.getRate()).build()).collect(Collectors.toList()));
                            }

                            //代理數量
                            affCommissionGroupVO.setAffiliateQuantity(
                                    iAffiliateService.lambdaQuery()
                                            .eq(Affiliate::getCommissionGroupId, commissionGroup.getId())
                                            .count()
                            );

                            return affCommissionGroupVO;
                        }
                );

        return PageResult.success(affCommissionGroupCombinationPage);
    }

    @Operation(summary = "找佣金群組Option")
    @GetMapping("/option")
    public Result<List<OptionVO<Long>>> findAffCommissionGroup() {
        return Result.success(iAffCommissionGroupService.list()
                .stream()
                .map(group -> OptionVO.<Long>builder().label(group.getName()).value(group.getId()).build())
                .collect(Collectors.toList())
        );
    }

    @Operation(summary = "新增佣金群組")
    @PostMapping
    public Result<Boolean> addAffCommissionGroup(@RequestBody AddCommissionGroupForm form) {
        List<AddOrModifyCommissionGroupForm> forms = form.getAddOrModifyAffCommissionGroupForms();

        CommissionGroup commissionGroup = CommissionGroup.builder()
                .name(form.getName())
                .details(
                        forms.stream()
                                .map(x -> CommissionGroupDetailVO.builder()
                                        .level(x.getLevel())
                                        .activityMember(x.getActivityMember())
                                        .minProfit(x.getMinProfit())
                                        .maxProfit(x.getMaxProfit())
                                        .rate(x.getRate())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();

        return Result.success(iAffCommissionGroupService.save(commissionGroup));
    }

    @Operation(summary = "修改佣金群組")
    @PutMapping
    public Result<Boolean> modifyAffCommissionGroup(@Validated @RequestBody ModifyCommissionGroupForm form) {
        List<AddOrModifyCommissionGroupForm> forms = form.getAddOrModifyAffCommissionGroupForms();
        CommissionGroup commissionGroup = CommissionGroup.builder()
                .id(form.getId())
                .name(form.getName())
                .details(
                        forms.stream()
                                .map(x -> CommissionGroupDetailVO.builder()
                                        .level(x.getLevel())
                                        .activityMember(x.getActivityMember())
                                        .minProfit(x.getMinProfit())
                                        .maxProfit(x.getMaxProfit())
                                        .rate(x.getRate())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();

        return Result.success(iAffCommissionGroupService.updateById(commissionGroup));
    }

    @Operation(summary = "轉移佣金群組")
    @PutMapping("/transfer")
    public Result<Boolean> transferAffCommissionGroup(@Validated @RequestBody TransferAffCommissionGroupForm form) {
        return Result.success(
                iAffiliateService.lambdaUpdate()
                        .eq(Affiliate::getCommissionGroupId, form.getSourceGroupId())
                        .set(Affiliate::getCommissionGroupId, form.getTargetGroupId())
                        .update()
        );
    }

}
