package com.c88.affiliate.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.affiliate.pojo.document.MemberTransactionsDocument;
import com.c88.affiliate.pojo.vo.AffiliateWinLossVO;
import com.c88.affiliate.pojo.vo.MemberWinLossVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.time.LocalDateTime;
import java.util.List;

public interface IMemberTransactionsRepository {

    MemberTransactionsDocument save(MemberTransactionsDocument entity);

    Boolean saveAll(List<MemberTransactionsDocument> toDocument);

    AffiliateWinLossVO findByParentIdWinLoss(Long parentId, LocalDateTime start, LocalDateTime end);

    IPage<MemberWinLossVO> findByDateWinLoss(String username, LocalDateTime start, LocalDateTime end);

    List<MemberTransactionsDocument> findByMemberId(long memberId);
}