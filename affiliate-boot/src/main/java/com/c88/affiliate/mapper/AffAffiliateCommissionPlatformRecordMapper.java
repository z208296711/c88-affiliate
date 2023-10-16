package com.c88.affiliate.mapper;

import com.c88.affiliate.pojo.entity.AffAffiliateCommissionPlatformRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author mac
* @description 针对表【aff_affiliate_commission_platform_record】的数据库操作Mapper
* @createDate 2022-12-13 18:21:48
* @Entity com.c88.affiliate.pojo.entity.AffAffiliateCommissionPlatformRecord
*/
public interface AffAffiliateCommissionPlatformRecordMapper extends BaseMapper<AffAffiliateCommissionPlatformRecord> {

    void insertBatch(List<AffAffiliateCommissionPlatformRecord> list);
}




