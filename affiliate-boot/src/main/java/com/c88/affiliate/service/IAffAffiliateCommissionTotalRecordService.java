package com.c88.affiliate.service;

import com.c88.affiliate.pojo.entity.AffAffiliateCommissionTotalRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.affiliate.pojo.vo.CommissionTotalCountVO;

import java.util.List;

/**
* @author mac
* @description 针对表【aff_affiliate_commission_total_record】的数据库操作Service
* @createDate 2022-12-09 10:53:15
*/
public interface IAffAffiliateCommissionTotalRecordService extends IService<AffAffiliateCommissionTotalRecord> {
    List<CommissionTotalCountVO> getCountList(String date);

    boolean resetLevel(String date, int level);

    boolean completeLevel(String date, int level);
}
