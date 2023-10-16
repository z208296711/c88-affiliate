package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.Label;
import com.c88.affiliate.pojo.vo.LabelVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LabelConverter {

    LabelVO toVO(Label entity);


}
