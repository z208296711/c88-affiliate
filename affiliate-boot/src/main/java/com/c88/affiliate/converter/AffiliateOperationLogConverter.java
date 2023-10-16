package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.entity.AffiliateOperationLog;
import com.c88.affiliate.pojo.vo.AffiliateOperationLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AffiliateOperationLogConverter {

    @Mappings({
            @Mapping(target = "username", source = "affiliateUsername")
    })
    AffiliateOperationLogVO toVO(AffiliateOperationLog entity);

}
