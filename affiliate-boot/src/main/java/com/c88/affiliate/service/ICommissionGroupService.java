package com.c88.affiliate.service;

import com.c88.affiliate.pojo.entity.CommissionGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
* @author user
* @description 针对表【aff_commission_group(佣金群組)】的数据库操作Service
* @createDate 2022-11-16 10:07:06
*/
public interface ICommissionGroupService extends IService<CommissionGroup> {
    BigDecimal getCommissionRate(Long groupId, int activeMembers, BigDecimal winLoss);
}
