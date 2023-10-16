package com.c88.affiliate.api.feign;

import com.c88.affiliate.api.dto.AffiliateMemberDTO;
import com.c88.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "c88-affiliate", path = "/affiliate/api/v1/affiliate/member")
public interface AffiliateMemberClient {

    @GetMapping
    Result<List<AffiliateMemberDTO>> findAffiliateMembers(@RequestParam List<Long> memberIds);

    @GetMapping("/parent/username")
    Result<List<AffiliateMemberDTO>> findAffiliateMembersByParentUsername(@RequestParam String parentUsername);

}
