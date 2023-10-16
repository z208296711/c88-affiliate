package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.CommissionGroup;
import com.c88.affiliate.pojo.vo.CommissionGroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommissionGroupConverter {

    CommissionGroupVO toVO(CommissionGroup entity);

}
