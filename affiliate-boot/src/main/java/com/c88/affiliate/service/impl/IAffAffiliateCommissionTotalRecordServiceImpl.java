package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.c88.affiliate.enums.AffiliateCommissionStatusEnum;
import com.c88.affiliate.enums.AffiliateCommissionTotalStateEnum;
import com.c88.affiliate.mapper.AffAffiliateCommissionRecordMapper;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionTotalRecord;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.vo.CommissionTotalCountVO;
import com.c88.affiliate.service.IAffAffiliateCommissionRecordService;
import com.c88.affiliate.service.IAffAffiliateCommissionTotalRecordService;
import com.c88.affiliate.mapper.AffAffiliateCommissionTotalRecordMapper;
import com.c88.affiliate.service.IAffiliateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author mac
* @description 针对表【aff_affiliate_commission_total_record】的数据库操作Service实现
* @createDate 2022-12-09 10:53:15
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class IAffAffiliateCommissionTotalRecordServiceImpl extends ServiceImpl<AffAffiliateCommissionTotalRecordMapper, AffAffiliateCommissionTotalRecord>
    implements IAffAffiliateCommissionTotalRecordService {

    private final IAffAffiliateCommissionRecordService affAffiliateCommissionRecordService;

    private final AffAffiliateCommissionRecordMapper affAffiliateCommissionRecordMapper;

    private final IAffAffiliateCommissionPlatformRecordServiceImpl affAffiliateCommissionPlatformRecordService;

    private final IAffiliateService affiliateService;


    @Override
    public List<CommissionTotalCountVO> getCountList(String date){
        List<CommissionTotalCountVO> commissionTotalCountVOS = affAffiliateCommissionRecordMapper.countTotal(date);
        Map<Integer, Integer> map = this.lambdaQuery().eq(AffAffiliateCommissionTotalRecord::getVerifyDate, date).list()
                .stream().collect(Collectors.toMap(AffAffiliateCommissionTotalRecord::getLevel, AffAffiliateCommissionTotalRecord::getStatus));
        commissionTotalCountVOS.forEach(c->{
            c.setStatus(map.get(c.getLevel()));
        });
        return commissionTotalCountVOS;
    }

    @Override
    public boolean resetLevel(String date, int level) {
        int totalLevel = 2; //TODO 未來要看實際有幾層，用DB查。現階段先固定兩層
        while(level<=totalLevel){
            List<Long> agentIds = affiliateService.lambdaQuery().eq(Affiliate::getLevel, level).list().stream().mapToLong(Affiliate::getId).boxed().collect(Collectors.toList());
            affAffiliateCommissionRecordService.deleteCommissionRecord(date, level);
            this.baseMapper.delete(new LambdaQueryWrapper<AffAffiliateCommissionTotalRecord>()
                    .eq(AffAffiliateCommissionTotalRecord::getLevel, level)
                    .eq(AffAffiliateCommissionTotalRecord::getVerifyDate, date));
            affAffiliateCommissionPlatformRecordService.deletePlatformRecord(agentIds, date);
            level +=1;
        }
        return true;
    }

    @Override
    public boolean completeLevel(String date, int level) {
//        AffAffiliateCommissionTotalRecord commissionTotalRecord = AffAffiliateCommissionTotalRecord.builder().verifyDate(date).level(level).status(AffiliateCommissionStatusEnum.VIRIFIED.getCode()).build();
        return this.lambdaUpdate().set(AffAffiliateCommissionTotalRecord::getStatus, AffiliateCommissionTotalStateEnum.CONFIRMED.getCode())
                .eq(AffAffiliateCommissionTotalRecord::getLevel,level).eq(AffAffiliateCommissionTotalRecord::getVerifyDate, date).update();
    }
}




