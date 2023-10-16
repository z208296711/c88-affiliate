package com.c88.affiliate.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.api.dto.AffiliateInfoDTO;
import com.c88.affiliate.api.dto.AuthAffiliateDTO;
import com.c88.affiliate.converter.AffiliateConverter;
import com.c88.affiliate.enums.AffiliateOperationTypeEnum;
import com.c88.affiliate.mapper.AffiliateMapper;
import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.entity.AffiliateOperationLog;
import com.c88.affiliate.pojo.entity.AffiliateProtectPasswordQuestion;
import com.c88.affiliate.pojo.entity.CommissionGroup;
import com.c88.affiliate.pojo.entity.DifferenceRecord;
import com.c88.affiliate.pojo.entity.MemberLabel;
import com.c88.affiliate.pojo.form.AffiliateForm;
import com.c88.affiliate.pojo.form.ModifyAffiliateForgotPasswordForm;
import com.c88.affiliate.pojo.form.RegisterAffiliateForm;
import com.c88.affiliate.pojo.form.SearchAffiliateForm;
import com.c88.affiliate.pojo.form.SwiftAffiliateForm;
import com.c88.affiliate.pojo.form.VerifyAffiliateForgotPasswordForm;
import com.c88.affiliate.pojo.vo.AddDifferenceRecordForm;
import com.c88.affiliate.pojo.vo.AdminAffiliateVO;
import com.c88.affiliate.pojo.vo.AffiliateMemberSubVO;
import com.c88.affiliate.pojo.vo.H5AffiliateVO;
import com.c88.affiliate.repository.IMemberTransactionsRepository;
import com.c88.affiliate.service.IAffMemberTransactionsService;
import com.c88.affiliate.service.IAffiliateMemberService;
import com.c88.affiliate.service.IAffiliateOperationLogService;
import com.c88.affiliate.service.IAffiliateProtectPasswordQuestionService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.affiliate.service.ICommissionGroupService;
import com.c88.affiliate.service.IDifferenceRecordService;
import com.c88.affiliate.service.IMemberLabelService;
import com.c88.affiliate.utils.BeanUtils;
import com.c88.common.core.desensitization.utils.DesensitizedUtils;
import com.c88.common.core.enums.EnableEnum;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.redis.utils.RedisUtils;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.UserUtils;
import com.c88.feign.AuthFeignClient;
import com.c88.member.api.MemberFeignClient;
import com.c88.member.dto.MemberInfoDTO;
import com.c88.member.dto.MemberLockDownDTO;
import com.c88.payment.client.PaymentClient;
import com.c88.payment.dto.PaymentAffiliateBalanceDTO;
import com.c88.sms.MailService;
import com.c88.sms.entity.SlackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.c88.affiliate.constants.RedisKey.FORGOT_PASSWORD_EMAIL;
import static com.c88.affiliate.constants.RedisKey.FORGOT_PASSWORD_MOBILE;
import static com.c88.affiliate.constants.SlackHooks.SEND_EMAIL_URL;
import static com.c88.affiliate.constants.SlackHooks.SEND_SMS_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliateServiceImpl extends ServiceImpl<AffiliateMapper, Affiliate> implements IAffiliateService {

    private final RestTemplate restTemplate;

    private final IAffiliateOperationLogService iAffiliateOperationLogService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final AffiliateConverter affiliateConverter;

    private final IMemberLabelService memberLabelService;

    private final IAffiliateMemberService iAffiliateMemberService;

    private final ICommissionGroupService iCommissionGroupService;

    private final MemberFeignClient memberFeignClient;

    private final AuthFeignClient authFeignClient;

    private final IAffiliateProtectPasswordQuestionService iAffiliateProtectPasswordQuestionService;

    private final IAffMemberTransactionsService iAffMemberTransactionsService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final IDifferenceRecordService iDifferenceRecordService;

    private final PaymentClient paymentClient;

    private final IMemberTransactionsRepository iMemberTransactionsRepository;

    @Value("${agent.promotion.link:http://abc.com}")
    private String agentUrl;

    @Value("${agent.promotion.reurl-api-key:aaaaa}")
    String reurlKey;

    @Value("${agent.promotion.api-url:https://abc.com}")
    String apiUrl;

    @Override
    public H5AffiliateVO getH5AffiliateVO(Long id) {
        Affiliate affiliate = Optional.ofNullable(this.getById(id))
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));

        int subordinateCount = iAffiliateMemberService.lambdaQuery()
                .select(AffiliateMember::getMemberId)
                .eq(AffiliateMember::getParentId, affiliate.getId())
                .count();

        H5AffiliateVO h5AffiliateVO = affiliateConverter.toH5AffiliateVO(affiliate);
        h5AffiliateVO.setSubordinateCount(subordinateCount);
        h5AffiliateVO.setWithdraw(StringUtils.isNotBlank(affiliate.getWithdrawPassword()) ? 1 : 0);
        h5AffiliateVO.setProtectPassword(iAffiliateProtectPasswordQuestionService.lambdaQuery().eq(AffiliateProtectPasswordQuestion::getUsername, affiliate.getUsername()).count());
        return h5AffiliateVO;
    }

    @Override
    public AuthAffiliateDTO getAuthAffiliateDTO(String username) {
        return this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .map(affiliateConverter::toDTO)
                .orElse(null);
    }

    @Override
    public AffiliateInfoDTO getAffiliateInfoDTO(Long id) {
        return this.lambdaQuery()
                .eq(Affiliate::getId, id)
                .oneOpt()
                .map(affiliateConverter::toInfoDTO)
                .orElse(null);
    }

    @Override
    public AffiliateInfoDTO getAffiliateInfoDTO(String username) {
        return this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .map(affiliateConverter::toInfoDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public Affiliate createAffiliate(AffiliateForm form) {
        // 判斷代理帳號是否存在
        this.lambdaQuery().eq(Affiliate::getUsername, form.getUsername())
                .oneOpt()
                .ifPresent(x -> {
                            throw new BizException(ResultCode.AFFILIATE_IS_EXIST);
                        }
                );

        Affiliate affiliate = affiliateConverter.toEntity(form);
        affiliate.setPassword(passwordEncoder.encode(form.getPassword()));
        affiliate.setWithdrawPassword(passwordEncoder.encode(form.getWithdrawPassword()));
        affiliate.setPromotionCode(this.createPromoteCode());

        if (StringUtils.isNotBlank(form.getMemberUsername())) {
            AffiliateMember affiliateMember = iAffiliateMemberService.lambdaQuery().eq(AffiliateMember::getMemberUsername, form.getMemberUsername())
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));
            affiliate.setMemberId(affiliateMember.getMemberId());

            Boolean isAffiliateLowLevel = this.isAffiliateLowLevel(form.getUsername(), form.getMemberUsername());
            if (Boolean.TRUE.equals(isAffiliateLowLevel)) {
                throw new BizException(ResultCode.IS_AFFILIATE_LOW_LEVEL);
            }
        }

        this.save(affiliate);

        if (StringUtils.isNotBlank(form.getParentUsername())) {
            Affiliate parentAffiliate = this.findByUsername(form.getParentUsername());
            affiliate.setParentId(parentAffiliate.getId());
            affiliate.setParents(parentAffiliate.getParents() + "," + affiliate.getId());
            affiliate.setParentUsername(parentAffiliate.getUsername());
            affiliate.setMasterUsername(parentAffiliate.getMasterUsername());
            affiliate.setMasterId(parentAffiliate.getMasterId());
            affiliate.setCommissionGroupId(parentAffiliate.getCommissionGroupId());
            affiliate.setLevel(2);
        } else {
            affiliate.setMasterId(affiliate.getId());
            affiliate.setMasterUsername(form.getUsername());
            affiliate.setParentId(affiliate.getId());
            affiliate.setParentUsername(form.getUsername());
            affiliate.setParents(affiliate.getId() + "");
        }
        this.updateById(affiliate);

        return affiliate;
    }

    @Override
    public Boolean register(RegisterAffiliateForm form) {
        authFeignClient.googleRecaptcha(form.getUsername(), form.getToken());
        Affiliate affiliate = new Affiliate();
        affiliate.setUsername(form.getUsername());
        affiliate.setPassword(passwordEncoder.encode(form.getPassword()));
        affiliate.setPromotionCode(this.createPromoteCode());
        affiliate.setEnable(EnableEnum.STOP.getCode());
        return this.save(affiliate);
    }

    private Boolean lockDownSubAffiliate(Affiliate affiliate) {

        List<Affiliate> affiliateList = this.lambdaQuery()
                .select(Affiliate::getId, Affiliate::getUsername, Affiliate::getEnable)
                .likeRight(Affiliate::getParents, affiliate.getParents() + ",")
                .list();

        affiliateList.forEach(x -> {
            authFeignClient.cleanToken("c88-affiliate", x.getUsername());
            x.setEnable(EnableEnum.STOP.getCode());
        });
        return this.updateBatchById(affiliateList);
    }

    private Boolean lockDownAffiliateMember(Affiliate affiliate) {
        List<Long> memberIdList = iAffiliateMemberService.lambdaQuery()
                .likeRight(AffiliateMember::getParents, affiliate.getParents())
                .select(AffiliateMember::getMemberId)
                .list()
                .stream()
                .map(AffiliateMember::getMemberId)
                .collect(Collectors.toList());
        MemberLockDownDTO memberLockDownDTO = new MemberLockDownDTO();
        memberLockDownDTO.setMemberIdList(memberIdList);
        Result<Boolean> lockDownRes = memberFeignClient.lockDown(memberLockDownDTO);
        return lockDownRes.getData() != null;
    }

    @Override
    @Transactional
    public Boolean editAffiliate(AffiliateForm form, String operator) {
        Affiliate originAffiliate = Optional.ofNullable(this.getById(form.getId()))
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));
        Affiliate affiliate = affiliateConverter.toEntity(form);
        //風控使用,紀錄最後修改時間
        if(!form.getRealName().equals(originAffiliate.getRealName())
        || !form.getIm().equals(originAffiliate.getIm())
        || !form.getEmail().equals(originAffiliate.getEmail())
        || !form.getMobile().equals(originAffiliate.getMobile())
        || !form.getMemberUsername().equals(originAffiliate.getMemberUsername())
          ){
            affiliate.setLastInfoModified(LocalDateTime.now());
        }
        if (StringUtils.isNotBlank(form.getPassword()) || StringUtils.isNotBlank(form.getWithdrawPassword())) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }

        if (StringUtils.isNotBlank(form.getMemberUsername())) {
            AffiliateMember affiliateMember = iAffiliateMemberService.lambdaQuery().eq(AffiliateMember::getMemberUsername, form.getMemberUsername())
                    .oneOpt()
                    .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));
            affiliate.setMemberId(affiliateMember.getMemberId());

            Boolean isAffiliateLowLevel = this.isAffiliateLowLevel(form.getUsername(), form.getMemberUsername());
            if (Boolean.TRUE.equals(isAffiliateLowLevel)) {
                throw new BizException(ResultCode.IS_AFFILIATE_LOW_LEVEL);
            }
        }

        if (form.getLevel() != null) {
            if (form.getLevel() == 1) {
                affiliate.setParentId(originAffiliate.getId());
                affiliate.setParents(String.valueOf(originAffiliate.getId()));
                affiliate.setParentUsername(originAffiliate.getUsername());

                affiliate.setMasterId(originAffiliate.getId());
                affiliate.setMasterUsername(originAffiliate.getUsername());
            } else {
                if (StringUtils.isBlank(form.getParentUsername())) {
                    throw new BizException(ResultCode.PARAM_ERROR);
                }
                Affiliate parentAffiliate = this.findByUsername(form.getParentUsername());
                affiliate.setMasterId(parentAffiliate.getMasterId());
                affiliate.setMasterUsername(parentAffiliate.getMasterUsername());
                affiliate.setParentId(parentAffiliate.getId());
                affiliate.setParents(parentAffiliate.getParents() + "," + affiliate.getId());
                affiliate.setParentUsername(parentAffiliate.getUsername());
                affiliate.setCommissionGroupId(parentAffiliate.getCommissionGroupId());

                List<AffiliateMember> affiliateMembers = iAffiliateMemberService.lambdaQuery()
                        .eq(AffiliateMember::getParentUsername, form.getParentUsername())
                        .list();
                if (CollectionUtils.isNotEmpty(affiliateMembers)) {
                    List<AffiliateMember> updateMembers = affiliateMembers
                            .stream()
                            .map(member -> {
                                member.setMasterUsername(affiliate.getMasterUsername());
                                member.setMasterId(affiliate.getMasterId());
                                member.setParentId(affiliate.getParentId());
                                member.setParentUsername(affiliate.getParentUsername());
                                member.setParents(affiliate.getParents());
                                return member;
                            }).collect(Collectors.toList());
                    iAffiliateMemberService.updateBatchById(updateMembers);

                    iAffMemberTransactionsService.lambdaUpdate()
                            .set(AffMemberTransactions::getMasterUsername, affiliate.getMasterUsername())
                            .set(AffMemberTransactions::getMasterId, affiliate.getMasterId())
                            .set(AffMemberTransactions::getParentId, affiliate.getParentId())
                            .set(AffMemberTransactions::getParents, affiliate.getParents())
                            .set(AffMemberTransactions::getParentUsername, affiliate.getParentUsername())
                            .eq(AffMemberTransactions::getParentUsername, form.getParentUsername())
                            .update();
                }

            }
        }

        if (form.getEnable() != null && form.getEnable().equals(EnableEnum.STOP.getCode())) {
            authFeignClient.cleanToken("c88-affiliate", originAffiliate.getUsername());
            this.lockDownSubAffiliate(originAffiliate);
            this.lockDownAffiliateMember(originAffiliate);
        }

        this.updateById(affiliate);
        Affiliate newAffiliate = this.getById(affiliate.getId());
        Map<String, Object> updateMap = BeanUtils.getChangedFields(affiliate, originAffiliate);
        this.addOperation(originAffiliate, newAffiliate, updateMap, operator);
        return true;
    }

    private void addOperation(Affiliate originAffiliate,
                              Affiliate afterAffiliate,
                              Map<String, Object> updateMap,
                              String operator) {
        updateMap.keySet().forEach(key -> {
            int type = 0;
            String content = String.valueOf(updateMap.getOrDefault(key, ""));
            switch (key) {
                case "parentUsername":
                    type = AffiliateOperationTypeEnum.EDIT_PARENT.getValue();
                    break;
                case "realName":
                    type = AffiliateOperationTypeEnum.EDIT_REAL_NAME.getValue();
                    break;
                case "email":
                    type = AffiliateOperationTypeEnum.EDIT_EMAIL.getValue();
                    break;
                case "mobile":
                    type = AffiliateOperationTypeEnum.EDIT_MOBILE.getValue();
                    break;
                case "level":
                    type = AffiliateOperationTypeEnum.SETTING_LEVEL.getValue();
                    if ("1".equals(content)) {
                        type = AffiliateOperationTypeEnum.SETTING_FIRST_LEVEL.getValue();
                    }
                    break;
                case "im":
                case "imType":
                    type = AffiliateOperationTypeEnum.EDIT_IM.getValue();
                    content = afterAffiliate.getImType() + "," + afterAffiliate.getIm();
                    break;
                case "enable":
                    type = AffiliateOperationTypeEnum.EDIT_ENABLE.getValue();
                    break;
                case "memberUsername":
                    type = AffiliateOperationTypeEnum.EDIT_MEMBER.getValue();
                    break;
            }

            // 設置第一層的時候,parentUsername也會異動 但紀錄需skip掉
            if (updateMap.containsKey("parentUsername") && Objects.equals(String.valueOf(updateMap.get("level")), "1") &&
                    key.equals("parentUsername")) {
                return;
            }
            //新增異動紀錄
            AffiliateOperationLog operationLog = new AffiliateOperationLog();
            operationLog.setType(type);
            operationLog.setAffiliateId(originAffiliate.getId());
            operationLog.setAffiliateUsername(originAffiliate.getUsername());
            operationLog.setContent(content);
            operationLog.setUpdateBy(operator);
            iAffiliateOperationLogService.save(operationLog);
        });


    }

    @Override
    public Boolean changePassword(Long affiliateId, String password) {
        Affiliate affiliate = this.getById(affiliateId);
        affiliate.setPassword(passwordEncoder.encode(password));
        return this.updateById(affiliate);
    }

    @Override
    public Boolean changePassword(Long affiliateId, String originPassword, String newPassword) {
        Affiliate affiliate = this.getById(affiliateId);
        if (!passwordEncoder.matches(originPassword, affiliate.getPassword())) {
            throw new BizException(ResultCode.OLD_PASSWORD_CONFIRM_ERROR);
        }
        affiliate.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(affiliate);
    }

    @Override
    public Boolean changeWithdrawPassword(Long affiliateId, String originPassword, String withdrawPassword) {
        Affiliate affiliate = this.getById(affiliateId);
        if (StringUtils.isNotBlank(affiliate.getWithdrawPassword())) {
            if (!passwordEncoder.matches(originPassword, affiliate.getWithdrawPassword())) {
                throw new BizException(ResultCode.OLD_PASSWORD_CONFIRM_ERROR);
            }
        }
        affiliate.setWithdrawPassword(passwordEncoder.encode(withdrawPassword));
        return this.updateById(affiliate);
    }

    @Override
    public Boolean changeWithdrawPassword(Long affiliateId, String withdrawPassword) {
        Affiliate affiliate = this.getById(affiliateId);
        affiliate.setWithdrawPassword(passwordEncoder.encode(withdrawPassword));
        return this.updateById(affiliate);
    }

    @Override
    public Boolean isAffiliateLowLevel(String parentUsername, String memberUsername) {
        Result<MemberInfoDTO> authUserDTOResult = memberFeignClient.getMemberInfo(memberUsername);
        if (!Result.isSuccess(authUserDTOResult)) {
            throw new BizException("be_Agent_list.alert01");
        }
        MemberInfoDTO authUserDTO = authUserDTOResult.getData();
        if (authUserDTO == null || !Objects.equals(authUserDTO.getStatus(), Byte.valueOf("1"))) {
            throw new BizException("be_Agent_list.alert01");
        }

        //檢查是否有重複
        Affiliate affiliate = this.lambdaQuery().eq(Affiliate::getUsername, parentUsername).one();
        //如果為空，代表是新增代理，不用檢查重覆問題
        if (affiliate != null && (StringUtils.isBlank(affiliate.getMemberUsername()) || !affiliate.getMemberUsername().equals(memberUsername))) {
            int count = this.lambdaQuery().eq(Affiliate::getMemberUsername, memberUsername).count();
            if (count != 0) {
                throw new BizException("be_Agent_list.alert05");
            }
        }

        return iAffiliateMemberService.lambdaQuery()
                .eq(AffiliateMember::getMemberUsername, memberUsername)
                .eq(AffiliateMember::getParentUsername, parentUsername)
                .oneOpt()
                .isPresent();
    }

    @Override
    public IPage<AdminAffiliateVO> findAffiliate(SearchAffiliateForm form) {
        return null;
    }

    @Override
    public IPage<AdminAffiliateVO> findAffiliatePage(SearchAffiliateForm form) {

        Map<Long, String> commissionGroupMap = iCommissionGroupService.list().stream().collect(Collectors.toMap(CommissionGroup::getId, CommissionGroup::getName));
        IPage<Affiliate> adminAffiliateIPage = this.lambdaQuery()
                .eq(form.getCommissionGroupId() != null, Affiliate::getCommissionGroupId, form.getCommissionGroupId())
                .eq(form.getEnable() != null, Affiliate::getEnable, form.getEnable())
                .eq(form.getLevel() != null, Affiliate::getLevel, form.getLevel())
                .eq(StringUtils.isNotBlank(form.getUsername()), Affiliate::getUsername, form.getUsername())
                .eq(StringUtils.isNotBlank(form.getPromotionCode()), Affiliate::getPromotionCode, form.getPromotionCode())
                .eq(StringUtils.isNotBlank(form.getRealName()), Affiliate::getRealName, form.getRealName())
                .eq(StringUtils.isNotBlank(form.getParentUsername()), Affiliate::getParentUsername, form.getParentUsername())
                .orderByDesc(Affiliate::getGmtCreate)
                .page(new Page<>(form.getPageNum(), form.getPageSize()));

        List<Long> idsList = adminAffiliateIPage.getRecords().stream().map(Affiliate::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(idsList)) {
            return new Page<>(form.getPageNum(), form.getPageSize());
        }

        Map<Long, Integer> map = iAffiliateMemberService.findMemberByParentId(idsList)
                .stream()
                .collect(Collectors.toMap(AffiliateMemberSubVO::getParentId, AffiliateMemberSubVO::getCount));

        Result<List<PaymentAffiliateBalanceDTO>> paymentAffiliateBalanceDTOResult = paymentClient.findPaymentAffiliateBalanceByAffiliateIdArray(idsList);
        Map<Long, BigDecimal> balanceMap = paymentAffiliateBalanceDTOResult.getData()
                .stream()
                .collect(Collectors.toMap(PaymentAffiliateBalanceDTO::getAffiliateId, PaymentAffiliateBalanceDTO::getBalance));

        return adminAffiliateIPage.convert(x -> affiliateConverter.toAdminAffiliateVO(x, commissionGroupMap, map, balanceMap, agentUrl));
    }

    @Override
    public Boolean findAffiliateForgotPasswordMobile(String username) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        if (StringUtils.isBlank(affiliate.getMobile())) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }

        String code = RandomUtil.randomString("0123456789", 5);

        //todo send sms

        restTemplate.postForEntity(SEND_SMS_URL,
                SlackMessage.builder().text(String.format("代理:%s 電話號碼:%s 驗證碼:%s", affiliate.getUsername(), affiliate.getMobile(), code)).build(),
                String.class);

        redisTemplate.opsForValue()
                .set(this.getForgetPasswordMobileKey(username, affiliate.getMobile()), code, 3, TimeUnit.MINUTES);

        return Boolean.TRUE;
    }

    @Override
    public Boolean verifyAffiliateForgotPasswordMobile(String username, VerifyAffiliateForgotPasswordForm form) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String key = this.getForgetPasswordMobileKey(username, affiliate.getMobile());
        String code = (String) redisTemplate.opsForValue().get(key);
        return form.getCode().equals(code);
    }

    @Override
    public Boolean findAffiliateForgotPasswordEmail(String username) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        if (StringUtils.isBlank(affiliate.getEmail())) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }

        String code = RandomUtil.randomString("0123456789", 5);

        String text = "Xin chào người dùng thân mến, dưới đây là mã xác minh e-mai: %s%nVui lòng hoàn thành xác thực trong %s phút, cảm ơn bạn !";
        CompletableFuture<Boolean> future = mailService.sendMail(
                new MailService.MailUnit()
                        .setTo(affiliate.getEmail())
                        .setSubject("Xác thực liên kết e-mail C88bet")
                        .setText(String.format(text, code, 60))
        );

        try {
            future.get();

            restTemplate.postForEntity(SEND_EMAIL_URL,
                    SlackMessage.builder().text(String.format("代理:%s 電子郵件:%s 驗證碼:%s", affiliate.getUsername(), affiliate.getEmail(), code)).build(),
                    String.class);

            redisTemplate.opsForValue()
                    .set(this.getForgetPasswordEmailKey(username, affiliate.getEmail()), code, 3, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("send email error: {}", e);
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean verifyAffiliateForgotPasswordEmail(String username, VerifyAffiliateForgotPasswordForm form) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String key = this.getForgetPasswordEmailKey(username, affiliate.getEmail());
        String code = (String) redisTemplate.opsForValue().get(key);

        return form.getCode().equals(code);
    }

    @Override
    public AdminAffiliateVO findAffiliate(Long id) {
        Map<Long, String> commissionGroupMap = iCommissionGroupService.list().stream().collect(Collectors.toMap(CommissionGroup::getId, CommissionGroup::getName));
        Affiliate affiliate = this.getById(id);
        Map<Long, Integer> map = iAffiliateMemberService.findMemberByParentId(List.of(affiliate.getId()))
                .stream()
                .collect(Collectors.toMap(AffiliateMemberSubVO::getParentId, AffiliateMemberSubVO::getCount));

        Result<List<PaymentAffiliateBalanceDTO>> paymentAffiliateBalanceDTOResult = paymentClient.findPaymentAffiliateBalanceByAffiliateIdArray(List.of(affiliate.getId()));
        Map<Long, BigDecimal> balanceMap = paymentAffiliateBalanceDTOResult.getData()
                .stream()
                .collect(Collectors.toMap(PaymentAffiliateBalanceDTO::getAffiliateId, PaymentAffiliateBalanceDTO::getBalance));
        return affiliateConverter.toAdminAffiliateVO(affiliate, commissionGroupMap, map, balanceMap, agentUrl);
    }

    @Override
    public String getAffiliateShortUrl(String url) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", url);
        jsonObject.put("utm_source", "FB_AD");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("reurl-api-key", reurlKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<String> response = new RestTemplate().postForEntity(apiUrl, requestEntity, String.class);
//        log.info("response:{}", response);
        JSONObject result = JSONObject.parseObject(response.getBody());
        return result.get("short_url").toString();
    }

    @Transactional
    public Boolean modifyAffiliateForgotPassword(String username, ModifyAffiliateForgotPasswordForm form) {
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BizException(ResultCode.PASSWORD_NEW_AND_CONFIRM_INCONSISTENT);
        }

        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String emailKey = this.getForgetPasswordEmailKey(username, affiliate.getEmail());
        String mobileKey = this.getForgetPasswordMobileKey(username, affiliate.getMobile());
        String emailCode = (String) redisTemplate.opsForValue().get(emailKey);
        String mobileCode = (String) redisTemplate.opsForValue().get(mobileKey);
        boolean emailResult = form.getVerifyCode().equals(emailCode);
        boolean mobileResult = form.getVerifyCode().equals(mobileCode);
        if (!emailResult && !mobileResult) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        redisTemplate.delete(List.of(emailKey, mobileKey));
        return this.lambdaUpdate()
                .eq(Affiliate::getUsername, username)
                .set(Affiliate::getPassword, passwordEncoder.encode(form.getNewPassword()))
                .update();
    }

    @Override
    public Boolean modifyAffiliateRealName(Long affiliateId, String realName) {
        return this.lambdaUpdate().eq(Affiliate::getId, affiliateId)
                .set(Affiliate::getRealName, realName)
                .set(Affiliate::getLastInfoModified, LocalDateTime.now())
                .update();
    }

    @Override
    public Boolean modifyMemberUsername(Long affiliateId, String memberUsername) {
        Affiliate affiliate = Optional.ofNullable(this.getById(affiliateId))
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        int count = this.lambdaQuery().eq(Affiliate::getMemberUsername, memberUsername).count();
        if (count != 0) {
            throw new BizException(ResultCode.MEMBER_ALREADY_BINDING_BY_AFFILIATE);
        }
        Boolean result = this.isAffiliateLowLevel(affiliate.getUsername(), memberUsername);
        if (Boolean.TRUE.equals(result)) {
            throw new BizException(ResultCode.IS_AFFILIATE_LOW_LEVEL);
        }
        affiliate.setMemberUsername(memberUsername);
        affiliate.setLastInfoModified(LocalDateTime.now());
        return this.updateById(affiliate);
    }

    @Override
    public Boolean modifyIMAccount(Long affiliateId, Integer imType, String imAccount) {
        return this.lambdaUpdate()
                .eq(Affiliate::getId, affiliateId)
                .set(Affiliate::getIm, imAccount)
                .set(Affiliate::getImType, imType)
                .set(Affiliate::getLastInfoModified, LocalDateTime.now())
                .update();
    }

    @Override
    public String getVerifyCodeByEmail(Long affiliateId, String email) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getId, affiliateId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String code = RandomUtil.randomString("0123456789", 5);

        String text = "Xin chào người dùng thân mến, dưới đây là mã xác minh e-mai: %s%nVui lòng hoàn thành xác thực trong %s phút, cảm ơn bạn !";
        CompletableFuture<Boolean> future = mailService.sendMail(
                new MailService.MailUnit()
                        .setTo(email)
                        .setSubject("Xác thực liên kết e-mail C88bet")
                        .setText(String.format(text, code, 60))
        );

        try {
            future.get();

            restTemplate.postForEntity(SEND_EMAIL_URL,
                    SlackMessage.builder().text(String.format("代理:%s 電子郵件:%s 驗證碼:%s", affiliate.getUsername(), email, code)).build(),
                    String.class);

            redisTemplate.opsForValue().set(this.getForgetPasswordEmailKey(affiliate.getUsername(), email), code, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info(ExceptionUtil.getMessage(e));
        }

        return DesensitizedUtils.email(email);
    }

    @Override
    public String getVerifyCodeBySMS(Long affiliateId, String phoneNumber) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getId, affiliateId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String code = RandomUtil.randomString("0123456789", 5);

        //todo send sms

        restTemplate.postForEntity(SEND_SMS_URL,
                SlackMessage.builder().text(String.format("代理:%s 電話號碼:%s 驗證碼:%s", affiliate.getUsername(), phoneNumber, code)).build(),
                String.class);

        redisTemplate.opsForValue()
                .set(this.getForgetPasswordMobileKey(affiliate.getUsername(), phoneNumber), code, 1, TimeUnit.MINUTES);

        return DesensitizedUtils.mobilePhone(phoneNumber);
    }

    @Override
    public Boolean modifyAffiliateEmail(Long affiliateId,
                                        String verifyCode,
                                        String email) {

        Affiliate affiliate = Optional.ofNullable(this.getById(affiliateId))
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String code = (String) redisTemplate.opsForValue().get(this.getForgetPasswordEmailKey(affiliate.getUsername(), StringUtils.isBlank(email) ? affiliate.getEmail() : email));
        if (!verifyCode.equals(code)) {
            throw new BizException(ResultCode.SMS_ERROR);
        }
        int count = this.lambdaQuery().eq(Affiliate::getEmail, email).count();
        if (count != 0) {
            throw new BizException(ResultCode.AFFILIATE_EMAIL_IS_BIND);
        }
        affiliate.setEmail(email);
        affiliate.setLastInfoModified(LocalDateTime.now());
        return this.updateById(affiliate);
    }

    @Override
    public Boolean modifyAffiliateMobile(Long affiliateId, String verifyCode, String mobile) {
        Affiliate affiliate = Optional.ofNullable(this.getById(affiliateId))
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        String code = (String) redisTemplate.opsForValue().get(this.getForgetPasswordMobileKey(affiliate.getUsername(), StringUtils.isBlank(mobile) ? affiliate.getMobile() : mobile));
        if (!verifyCode.equals(code)) {
            throw new BizException(ResultCode.SMS_ERROR);
        }
        int count = this.lambdaQuery().eq(Affiliate::getMobile, mobile).count();
        if (count != 0) {
            throw new BizException(ResultCode.AFFILIATE_MOBILE_IS_BIND);
        }
        affiliate.setMobile(mobile);
        affiliate.setLastInfoModified(LocalDateTime.now());
        return this.updateById(affiliate);
    }

    @Override
    @Transactional
    public Boolean doSwiftMember(SwiftAffiliateForm form) {
        Affiliate affiliate = this.findByUsername(form.getParentUsername());
        List<AffiliateMember> affiliateMembers = iAffiliateMemberService.lambdaQuery()
                .in(AffiliateMember::getMemberId, form.getMemberIds())
                .list();
        if (CollectionUtils.isEmpty(affiliateMembers)) {
            return true;
        }

        List<AffiliateMember> updateMembers = affiliateMembers
                .stream()
                .map(member -> {
                    member.setMasterUsername(affiliate.getMasterUsername());
                    member.setMasterId(affiliate.getMasterId());
                    member.setParentId(affiliate.getId());
                    member.setParentUsername(affiliate.getUsername());
                    member.setParents(affiliate.getParents());
                    return member;
                }).collect(Collectors.toList());
        iAffiliateMemberService.updateBatchById(updateMembers);

        iAffMemberTransactionsService.lambdaUpdate()
                .set(AffMemberTransactions::getMasterUsername, affiliate.getMasterUsername())
                .set(AffMemberTransactions::getMasterId, affiliate.getMasterId())
                .set(AffMemberTransactions::getParentId, affiliate.getId())
                .set(AffMemberTransactions::getParents, affiliate.getParents())
                .set(AffMemberTransactions::getParentUsername, affiliate.getUsername())
                .in(AffMemberTransactions::getMemberId, form.getMemberIds())
                .update();

        form.getMemberIds().stream().forEach(x -> docHandler(x, affiliate));

        memberLabelService.remove(Wrappers.<MemberLabel>lambdaQuery()
                .in(MemberLabel::getMemberId, form.getMemberIds()));
        return true;
    }

    @Override
    @Transactional
    public Boolean modifyDifferenceRecord(AddDifferenceRecordForm form) {
        Affiliate affiliate = this.lambdaQuery()
                .eq(Affiliate::getUsername, form.getUsername())
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.AFFILIATE_NOT_EXIST));

        BigDecimal before = affiliate.getDifference();
        BigDecimal after = affiliate.getDifference().add(form.getAmount());

        DifferenceRecord entity = new DifferenceRecord();
        entity.setAffiliateId(affiliate.getId());
        entity.setUsername(affiliate.getUsername());
        entity.setBeforeAmount(before);
        entity.setAmount(form.getAmount());
        entity.setAfterAmount(after);
        entity.setNote(form.getNote());
        entity.setModifiedBy(UserUtils.getUsername());

        affiliate.setDifference(after);
        this.updateById(affiliate);
        return iDifferenceRecordService.save(entity);
    }

    @Override
    public List<Affiliate> getSubAffiliateWithSelfById(Long agentId) {
        QueryWrapper<Affiliate> queryWrapper = new QueryWrapper<Affiliate>();
        queryWrapper.like("concat(',',parents,',')", "," + agentId + ",");
        return this.getBaseMapper().selectList(queryWrapper);
    }

    private Affiliate findByUsername(String username) {
        return this.lambdaQuery()
                .eq(Affiliate::getUsername, username)
                .oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
    }

    private String createPromoteCode() {
        String promoteCode = RandomStringUtils.randomNumeric(6);
        while (true) {
            int count = this.lambdaQuery().eq(Affiliate::getPromotionCode, promoteCode).count();
            if (count == 0) {
                return promoteCode;
            }
        }
    }

    /**
     * 取得忘記密碼手機找回 RedisKey
     *
     * @param username 代理帳號
     * @param mobile   手機號
     * @return
     */
    private String getForgetPasswordMobileKey(String username, String mobile) {
        return RedisUtils.buildKey(FORGOT_PASSWORD_MOBILE, username, mobile);
    }

    /**
     * 取得忘記密碼郵箱找回 RedisKey
     *
     * @param username 代理帳號
     * @param email    郵箱
     * @return
     */
    private String getForgetPasswordEmailKey(String username, String email) {
        return RedisUtils.buildKey(FORGOT_PASSWORD_EMAIL, username, email);
    }

    private void docHandler(long memberId, Affiliate affiliate) {
        List<MemberTransactionsDocument> docs = iMemberTransactionsRepository.findByMemberId(memberId);
        docs.forEach(x -> {
            x.setMasterId(affiliate.getMasterId());
            x.setMasterUsername(affiliate.getMasterUsername());
            x.setParentId(affiliate.getId());
            x.setParents(affiliate.getParents());
            x.setParentUsername(affiliate.getUsername());
        });
        iMemberTransactionsRepository.saveAll(docs);
    }
}




