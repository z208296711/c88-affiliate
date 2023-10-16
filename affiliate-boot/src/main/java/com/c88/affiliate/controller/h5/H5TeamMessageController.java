package com.c88.affiliate.controller.h5;

import com.c88.affiliate.enums.TeamMessageSearchTimeTypeEnum;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.vo.FindTeamMessageForm;
import com.c88.affiliate.pojo.vo.TeamMessageDetailVO;
import com.c88.affiliate.pojo.vo.TeamMessageVO;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import com.c88.payment.client.MemberBalanceClient;
import com.c88.payment.dto.MemberBalanceInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "『前台』團隊訊息")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/team/message")
public class H5TeamMessageController {

    private final IAffiliateService iAffiliateService;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final MemberBalanceClient memberBalanceClient;

    @Operation(summary = "團隊訊息")
    @GetMapping
    public Result<TeamMessageVO> findTeamMessage(@ParameterObject FindTeamMessageForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        LocalDateTime startTime = LocalDateTime.MIN;
        LocalDateTime endTime = LocalDateTime.MAX;

        LocalDate now = LocalDate.now();
        switch (TeamMessageSearchTimeTypeEnum.getEnum(form.getSearchTimeType())) {
            case TODAY:
                startTime = LocalDateTime.of(now, LocalTime.MIN);
                endTime = LocalDateTime.of(now, LocalTime.MAX);
                break;
            case YESTERDAY:
                startTime = LocalDateTime.of(now.minusDays(1), LocalTime.MIN);
                endTime = LocalDateTime.of(now.minusDays(1), LocalTime.MAX);
                break;
            case CURRENT_MONTH:
                startTime = LocalDateTime.of(now.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
                endTime = LocalDateTime.of(now.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
                break;
            case LAST_MONTH:
                startTime = LocalDateTime.of(now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
                endTime = LocalDateTime.of(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
                break;
            default:
        }

        List<Affiliate> affiliates = iAffiliateService.lambdaQuery()
                .eq(StringUtils.isNotBlank(form.getUsername()), Affiliate::getUsername, form.getUsername())
                .eq(Affiliate::getParentId, affiliateId)
                .or()
                .eq(Affiliate::getId, affiliateId)
                .list();

        if (CollectionUtils.isEmpty(affiliates)) {
            return Result.success(TeamMessageVO.builder().build());
        }

        Affiliate affiliate = iAffiliateService.lambdaQuery()
                .eq(Affiliate::getId, affiliateId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        List<AffiliateMember> members = iAffiliateMemberService.lambdaQuery()
                .in(AffiliateMember::getParentId, affiliates.stream().map(Affiliate::getId).collect(Collectors.toList()))
                .list();

        // 取得會員錢包訊息
        List<MemberBalanceInfoDTO> memberBalanceInfos = Collections.emptyList();
        Result<List<MemberBalanceInfoDTO>> memberBalanceInfoResult = memberBalanceClient.findMemberBalanceInfoByIds(members.stream().map(AffiliateMember::getMemberId).collect(Collectors.toList()));
        if (Result.isSuccess(memberBalanceInfoResult)) {
            memberBalanceInfos = memberBalanceInfoResult.getData();
        }
        LocalDateTime finalStartTime = startTime;
        LocalDateTime finalEndTime = endTime;
        List<MemberBalanceInfoDTO> finalMemberBalanceInfos = memberBalanceInfos;
        List<TeamMessageDetailVO> details = affiliates.stream()
                .map(aff -> {
                            List<Long> balanceMemberId = finalMemberBalanceInfos.stream()
                                    .filter(filter -> Objects.nonNull(filter.getFirstRechargeTime()))
                                    .map(MemberBalanceInfoDTO::getMemberId)
                                    .collect(Collectors.toList());

                            long firstRechargeMemberCount = members.stream()
                                    .filter(filter -> filter.getRegisterTime().compareTo(finalStartTime) > 0 && filter.getRegisterTime().compareTo(finalEndTime) < 0)
                                    .filter(filter -> Objects.equals(filter.getParentId(), aff.getId()) && balanceMemberId.contains(filter.getMemberId()))
                                    .count();

                            return TeamMessageDetailVO.builder()
                                    .username(aff.getUsername())
                                    .memberCount(members.stream()
                                            .filter(filter -> filter.getRegisterTime().compareTo(finalStartTime) > 0 && filter.getRegisterTime().compareTo(finalEndTime) < 0)
                                            .filter(filter -> Objects.equals(filter.getParentId(), aff.getId()))
                                            .count()
                                    )
                                    .firstRechargeMemberCount(firstRechargeMemberCount)
                                    .joinTeamTime(aff.getGmtCreate())
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        // 放入page
        PageResult.Data<TeamMessageDetailVO> detailPage = PageResult.Data.<TeamMessageDetailVO>builder()
                .total(details.size())
                .pageNum(form.getPageNum())
                .pageSize(form.getPageSize())
                .list(details.stream()
                        .skip((long) (form.getPageNum() - 1) * form.getPageSize())
                        .limit(form.getPageSize())
                        .collect(Collectors.toList())
                )
                .build();

        return Result.success(
                TeamMessageVO.builder()
                        .username(affiliate.getUsername())
                        .masterUsername(affiliate.getMasterUsername())
                        .parentCount(affiliates.size())
                        .memberCount(members.size())
                        .firstRechargeMemberCount(details.stream().mapToLong(TeamMessageDetailVO::getFirstRechargeMemberCount).sum())
                        .pageDetail(detailPage)
                        .build()
        );
    }

}
