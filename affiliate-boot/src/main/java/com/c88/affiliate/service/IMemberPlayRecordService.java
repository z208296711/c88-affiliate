package com.c88.affiliate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.affiliate.pojo.form.SearchMemberDepositRecordForm;
import com.c88.affiliate.pojo.form.SearchMemberPlayingRecordForm;
import com.c88.affiliate.pojo.vo.*;
import com.c88.common.core.result.PageResult;
import com.c88.common.core.result.Result;
import com.c88.payment.vo.MemberDepositDTO;
import com.c88.payment.vo.MemberRechargeDTO;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface IMemberPlayRecordService {

    IPage<H5MemberPlayingRecordVO> findMemberPlayRecord(SearchMemberPlayingRecordForm form);

    public PageResult<MemberDepositDTO> findMemberDepositRecord(SearchMemberDepositRecordForm form);

}
