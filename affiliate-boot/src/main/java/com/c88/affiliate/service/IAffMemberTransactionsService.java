package com.c88.affiliate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;

import java.util.List;

/**
 *
 */
public interface IAffMemberTransactionsService extends IService<AffMemberTransactions> {

    int countActiveMember(List<Long> agents, String date);
}
