package com.c88.affiliate.service;

import com.c88.affiliate.pojo.entity.AffAffiliateCommissionPlatformRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.affiliate.pojo.form.FindPerformanceReportExportForm;
import com.c88.affiliate.pojo.form.FindPerformanceReportForm;
import com.c88.affiliate.pojo.form.SearchAffiliateTeamTransForm;
import com.c88.affiliate.pojo.vo.AffiliateCommissionTeamReportVO;
import com.c88.affiliate.pojo.vo.AffliateCommissionPersonReportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportExportVO;
import com.c88.affiliate.pojo.vo.PerformanceReportVO;
import com.c88.common.core.result.PageResult;

import java.util.List;

/**
* @author mac
* @description 针对表【aff_affiliate_commission_record】的数据库操作Service
* @createDate 2022-12-08 14:25:05
*/
public interface IAffAffiliateCommissionRecordService extends IService<AffAffiliateCommissionRecord> {
//    void verifyCommission(int level, String date);

    int createCommission(int level, String date);

    boolean verifyCommission(Long id, int status);

    int deleteCommissionRecord(String date, int level);

    Boolean issueCommission(String date);

    AffliateCommissionPersonReportVO getPersonalCommission(String date);

    PageResult<AffiliateCommissionTeamReportVO> getTeamCommission(SearchAffiliateTeamTransForm form);

//    List<AffiliatePlatformDetailVO> getPlatformDetail(Long agentId, String date);
    List<AffAffiliateCommissionPlatformRecord> getPlatformDetail(String date);

    IPage<PerformanceReportVO> findPerformanceReport(FindPerformanceReportForm form);

    List<PerformanceReportExportVO> exportPerformanceReport(FindPerformanceReportExportForm form);
}
