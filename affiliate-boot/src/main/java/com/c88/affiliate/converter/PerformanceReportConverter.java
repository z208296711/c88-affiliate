package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.PerformanceReport;
import com.c88.affiliate.pojo.vo.PerformanceReportVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PerformanceReportConverter {

    PerformanceReportVO toVO(PerformanceReport entity);

}
