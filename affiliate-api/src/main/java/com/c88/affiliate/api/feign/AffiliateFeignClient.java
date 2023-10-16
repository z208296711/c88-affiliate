package com.c88.affiliate.api.feign;

import com.c88.affiliate.api.dto.AffiliateInfoDTO;
import com.c88.affiliate.api.dto.AuthAffiliateDTO;
import com.c88.affiliate.api.dto.CheckAffiliateLowLevelDTO;
import com.c88.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "c88-affiliate", path = "/affiliate")
public interface AffiliateFeignClient {

    @GetMapping("/api/v1/affiliate/username/{username}")
    Result<AuthAffiliateDTO> getAffiliateByUsername(@PathVariable String username);

    @GetMapping("/api/v1/affiliate/info/affiliateId/{id}")
    Result<AffiliateInfoDTO> getAffiliateInfoById(@PathVariable Long id);

    @GetMapping("/api/v1/affiliate/info/username/{username}")
    Result<AffiliateInfoDTO> getAffiliateInfoByUsername(@PathVariable String username);

    @PostMapping("/api/v1/affiliate/check/level/low/member")
    Result<Boolean> isAffiliateLowLevel(@RequestBody CheckAffiliateLowLevelDTO form);

    @PutMapping(value = "/api/v1/affiliate/{affiliateId}/{realName}")
    Result<Boolean> modifyAffiliateRealName(@PathVariable Long affiliateId, @PathVariable String realName);

}
