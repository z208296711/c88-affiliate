package com.c88.affiliate.controller.h5;

import com.c88.affiliate.converter.AffiliateProtectPasswordQuestionConverter;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.pojo.entity.AffiliateProtectPasswordQuestion;
import com.c88.affiliate.pojo.form.AddAffiliateProtectPasswordQuestionForm;
import com.c88.affiliate.pojo.form.ModifyAffiliateProtectPasswordQuestionForm;
import com.c88.affiliate.pojo.form.ValidAffiliateProtectPasswordQuestionForm;
import com.c88.affiliate.pojo.form.ValidAffiliateProtectPasswordQuestionPutPasswordForm;
import com.c88.affiliate.pojo.vo.AffiliateProtectPasswordQuestionVO;
import com.c88.affiliate.service.IAffiliateProtectPasswordQuestionService;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.AffiliateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Tag(name = "『前台』找回密碼-密保")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/affiliate/protect/password/question")
public class AffiliateProtectPasswordQuestionController {

    private final IAffiliateService iAffiliateService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final IAffiliateProtectPasswordQuestionService iAffiliateProtectPasswordQuestionService;

    private final AffiliateProtectPasswordQuestionConverter affiliateProtectPasswordQuestionConverter;

    @Operation(summary = "找密保問題")
    @GetMapping("/question/username")
    public Result<AffiliateProtectPasswordQuestionVO> findAffiliateProtectPasswordQuestion() {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        return Result.success(iAffiliateProtectPasswordQuestionService.lambdaQuery()
                .select(AffiliateProtectPasswordQuestion::getQuestion1,
                        AffiliateProtectPasswordQuestion::getQuestion2,
                        AffiliateProtectPasswordQuestion::getQuestion3)
                .eq(AffiliateProtectPasswordQuestion::getParentId, affiliateId)
                .oneOpt()
                .map(affiliateProtectPasswordQuestionConverter::toVO)
                .orElse(null)
        );
    }

    @Operation(summary = "找密保問題", description = "代理帳號")
    @GetMapping("/question/username/{username}")
    public Result<AffiliateProtectPasswordQuestionVO> findAffiliateProtectPasswordQuestion(@PathVariable("username") String username) {
        return Result.success(iAffiliateProtectPasswordQuestionService.lambdaQuery()
                .select(AffiliateProtectPasswordQuestion::getQuestion1,
                        AffiliateProtectPasswordQuestion::getQuestion2,
                        AffiliateProtectPasswordQuestion::getQuestion3)
                .eq(AffiliateProtectPasswordQuestion::getUsername, username)
                .oneOpt()
                .map(affiliateProtectPasswordQuestionConverter::toVO)
                .orElse(null)
        );
    }

    @Operation(summary = "驗證密保問題")
    @PostMapping("/valid/question")
    public Result<Boolean> validAffiliateProtectPasswordQuestion(@Validated @RequestBody ValidAffiliateProtectPasswordQuestionForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        return Result.success(iAffiliateProtectPasswordQuestionService.lambdaQuery()
                .eq(AffiliateProtectPasswordQuestion::getParentId, affiliateId)
                .eq(AffiliateProtectPasswordQuestion::getAnswer1, form.getAnswer1())
                .eq(AffiliateProtectPasswordQuestion::getAnswer2, form.getAnswer2())
                .eq(AffiliateProtectPasswordQuestion::getAnswer3, form.getAnswer3())
                .oneOpt()
                .isPresent()
        );
    }

    @Operation(summary = "驗證密保問題")
    @PostMapping("/valid/question/{username}")
    public Result<Boolean> validAffiliateProtectPasswordQuestion(@PathVariable() String username, @Validated @RequestBody ValidAffiliateProtectPasswordQuestionForm form) {
        return Result.success(iAffiliateProtectPasswordQuestionService.lambdaQuery()
                .eq(AffiliateProtectPasswordQuestion::getUsername, username)
                .eq(AffiliateProtectPasswordQuestion::getAnswer1, form.getAnswer1())
                .eq(AffiliateProtectPasswordQuestion::getAnswer2, form.getAnswer2())
                .eq(AffiliateProtectPasswordQuestion::getAnswer3, form.getAnswer3())
                .oneOpt()
                .isPresent()
        );
    }

    @Operation(summary = "新增密保")
    @PostMapping
    public Result<Boolean> addAffiliateProtectPasswordQuestion(@Validated @RequestBody AddAffiliateProtectPasswordQuestionForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        String username = AffiliateUtils.getUsername();
        if (Objects.isNull(affiliateId) || StringUtils.isBlank(username)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        AffiliateProtectPasswordQuestion affiliateProtectPasswordQuestion = affiliateProtectPasswordQuestionConverter.toEntity(form);
        affiliateProtectPasswordQuestion.setParentId(affiliateId);
        affiliateProtectPasswordQuestion.setUsername(username);

        return Result.success(iAffiliateProtectPasswordQuestionService.save(affiliateProtectPasswordQuestion));
    }

    @Operation(summary = "修改密保")
    @PutMapping
    public Result<Boolean> modifyAffiliateProtectPasswordQuestion(@Validated @RequestBody ModifyAffiliateProtectPasswordQuestionForm form) {
        Long affiliateId = AffiliateUtils.getAffiliateId();
        if (Objects.isNull(affiliateId)) {
            throw new BizException(ResultCode.AFFILIATE_NOT_EXIST);
        }

        return Result.success(iAffiliateProtectPasswordQuestionService.lambdaUpdate()
                .eq(AffiliateProtectPasswordQuestion::getParentId, affiliateId)
                .set(AffiliateProtectPasswordQuestion::getQuestion1, form.getQuestion1())
                .set(AffiliateProtectPasswordQuestion::getAnswer1, form.getAnswer1())
                .set(AffiliateProtectPasswordQuestion::getQuestion2, form.getQuestion2())
                .set(AffiliateProtectPasswordQuestion::getAnswer2, form.getAnswer2())
                .set(AffiliateProtectPasswordQuestion::getQuestion3, form.getQuestion3())
                .set(AffiliateProtectPasswordQuestion::getAnswer3, form.getAnswer3())
                .update()
        );
    }

    @Operation(summary = "驗證密保問題成功後可修改密碼")
    @PutMapping("/valid/question/put/password/{username}")
    public Result<Boolean> validAffiliateProtectPasswordQuestionPutPassword(@PathVariable("username") String username, @Validated @RequestBody ValidAffiliateProtectPasswordQuestionPutPasswordForm form) {
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BizException(ResultCode.PASSWORD_NEW_AND_CONFIRM_INCONSISTENT);
        }

        AtomicReference<Boolean> result = new AtomicReference<>(Boolean.FALSE);
        iAffiliateProtectPasswordQuestionService.lambdaQuery()
                .eq(AffiliateProtectPasswordQuestion::getUsername, username)
                .eq(AffiliateProtectPasswordQuestion::getAnswer1, form.getAnswer1())
                .eq(AffiliateProtectPasswordQuestion::getAnswer2, form.getAnswer2())
                .eq(AffiliateProtectPasswordQuestion::getAnswer3, form.getAnswer3())
                .oneOpt()
                .ifPresent(x -> {

                            boolean update = iAffiliateService.lambdaUpdate()
                                    .eq(Affiliate::getUsername, username)
                                    .set(Affiliate::getPassword, passwordEncoder.encode(form.getNewPassword()))
                                    .update();

                            if (Boolean.TRUE.equals(update)) {
                                result.set(Boolean.TRUE);
                            }
                        }
                );

        return Result.success(result.get());
    }

}
