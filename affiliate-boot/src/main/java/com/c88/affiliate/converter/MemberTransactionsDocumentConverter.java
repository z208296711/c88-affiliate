package com.c88.affiliate.converter;

import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import com.c88.affiliate.pojo.entity.AffMemberTransactions;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberTransactionsDocumentConverter {


  MemberTransactionsDocument toDocument(AffMemberTransactions entity);
  List<MemberTransactionsDocument> toDocument(List<AffMemberTransactions> entity);

}
