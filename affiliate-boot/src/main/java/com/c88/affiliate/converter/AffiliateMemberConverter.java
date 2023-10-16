package com.c88.affiliate.converter;

import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.affiliate.pojo.entity.AffiliateMember;
import com.c88.affiliate.pojo.vo.SwiftMemberVO;
import com.c88.affiliate.pojo.vo.H5AffiliateMemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AffiliateMemberConverter {

    AffiliateMemberDTO toDTO(AffiliateMember entity);
    H5AffiliateMemberVO toAffiliateMemberVO(AffiliateMember entity);

    SwiftMemberVO toSwiftVO(AffiliateMember entity);

}
