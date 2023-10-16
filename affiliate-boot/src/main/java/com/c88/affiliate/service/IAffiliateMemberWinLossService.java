package com.c88.affiliate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.affiliate.pojo.form.SearchAffiliateWinLossForm;
import com.c88.affiliate.pojo.form.SearchMemberWinLossForm;
import com.c88.affiliate.pojo.vo.AffiliateWinLossVO;
import com.c88.affiliate.pojo.vo.MemberWinLossVO;

/**
 * @description: TODO
 * @author: marcoyang
 * @date: 2022/12/26
 **/
public interface IAffiliateMemberWinLossService {

    IPage<AffiliateWinLossVO> getAffiliateWinLoss(SearchAffiliateWinLossForm form);

    IPage<MemberWinLossVO> getMemberWinLoss(SearchMemberWinLossForm form);
}
