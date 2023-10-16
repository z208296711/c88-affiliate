package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.DifferenceRecord;
import com.c88.affiliate.pojo.vo.DifferenceRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DifferenceRecordConverter {


    DifferenceRecordVO toVO(DifferenceRecord entity);

    DifferenceRecord toEntity(DifferenceRecordVO vo);

}
