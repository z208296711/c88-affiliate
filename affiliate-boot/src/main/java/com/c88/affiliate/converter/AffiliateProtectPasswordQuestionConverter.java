package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.AffiliateProtectPasswordQuestion;
import com.c88.affiliate.pojo.form.AddAffiliateProtectPasswordQuestionForm;
import com.c88.affiliate.pojo.vo.AffiliateProtectPasswordQuestionVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AffiliateProtectPasswordQuestionConverter {

    AffiliateProtectPasswordQuestionVO toVO(AffiliateProtectPasswordQuestion entity);

    AffiliateProtectPasswordQuestion toEntity(AddAffiliateProtectPasswordQuestionForm form);
}
