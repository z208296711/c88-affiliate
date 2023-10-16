package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionPlatformRecord;
import com.c88.affiliate.service.IAffAffiliateCommissionPlatformRecordService;
import com.c88.affiliate.mapper.AffAffiliateCommissionPlatformRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author mac
* @description 针对表【aff_affiliate_commission_platform_record】的数据库操作Service实现
* @createDate 2022-12-13 18:21:48
*/
@Service
@AllArgsConstructor
public class IAffAffiliateCommissionPlatformRecordServiceImpl extends ServiceImpl<AffAffiliateCommissionPlatformRecordMapper, AffAffiliateCommissionPlatformRecord>
    implements IAffAffiliateCommissionPlatformRecordService {

    public int deletePlatformRecord(List<Long> agentIds, String date){
        return this.baseMapper.delete(new LambdaQueryWrapper<AffAffiliateCommissionPlatformRecord>().eq(AffAffiliateCommissionPlatformRecord::getIssueDate, date)
                .in(AffAffiliateCommissionPlatformRecord::getAgentId, agentIds));
    }
}




