package com.c88.affiliate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.c88.affiliate.api.dto.AffiliateInfoDTO;
import com.c88.affiliate.api.dto.AuthAffiliateDTO;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.form.*;
import com.c88.affiliate.pojo.vo.AddDifferenceRecordForm;
import com.c88.affiliate.pojo.vo.AdminAffiliateVO;
import com.c88.affiliate.pojo.vo.H5AffiliateVO;

import java.util.List;

/**
 *
 */
public interface IAffiliateService extends IService<Affiliate> {

    H5AffiliateVO getH5AffiliateVO(Long id);

    AuthAffiliateDTO getAuthAffiliateDTO(String username);

    AffiliateInfoDTO getAffiliateInfoDTO(Long id);

    AffiliateInfoDTO getAffiliateInfoDTO(String username);

    Affiliate createAffiliate(AffiliateForm form);

    Boolean register(RegisterAffiliateForm form);

    Boolean editAffiliate(AffiliateForm form, String operator);

    Boolean changePassword(Long affiliateId, String password);

    Boolean changePassword(Long affiliateId, String originPassword, String newPassword);

    Boolean changeWithdrawPassword(Long affiliateId, String originPassword, String withdrawPassword);

    Boolean changeWithdrawPassword(Long affiliateId, String withdrawPassword);

    Boolean isAffiliateLowLevel(String parentUsername, String memberUsername);

    IPage<AdminAffiliateVO> findAffiliate(SearchAffiliateForm form);

    Boolean findAffiliateForgotPasswordMobile(String username);

    Boolean verifyAffiliateForgotPasswordMobile(String username, VerifyAffiliateForgotPasswordForm form);

    Boolean findAffiliateForgotPasswordEmail(String username);

    Boolean verifyAffiliateForgotPasswordEmail(String username, VerifyAffiliateForgotPasswordForm form);

    IPage<AdminAffiliateVO> findAffiliatePage(SearchAffiliateForm form);

    AdminAffiliateVO findAffiliate(Long id);

    String getAffiliateShortUrl(String url);

    Boolean modifyAffiliateForgotPassword(String username, ModifyAffiliateForgotPasswordForm form);

    Boolean modifyAffiliateRealName(Long affiliateId, String realName);

    Boolean modifyMemberUsername(Long affiliateId, String account);

    Boolean modifyIMAccount(Long affiliateId, Integer imType, String imAccount);

    String getVerifyCodeByEmail(Long affiliateId, String email);

    String getVerifyCodeBySMS(Long affiliateId, String mobile);

    Boolean modifyAffiliateEmail(Long affiliateId,
                                 String verifyCode,
                                 String email);

    Boolean modifyAffiliateMobile(Long affiliateId,
                                  String verifyCode,
                                  String mobile);

    Boolean doSwiftMember(SwiftAffiliateForm form);

    Boolean modifyDifferenceRecord(AddDifferenceRecordForm form);

    List<Affiliate> getSubAffiliateWithSelfById(Long agentId);
}
