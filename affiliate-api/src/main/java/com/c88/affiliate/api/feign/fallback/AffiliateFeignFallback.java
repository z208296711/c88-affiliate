package com.c88.affiliate.api.feign.fallback;

import com.c88.affiliate.api.dto.AffiliateInfoDTO;
import com.c88.affiliate.api.dto.AuthAffiliateDTO;
import com.c88.affiliate.api.dto.CheckAffiliateLowLevelDTO;
import com.c88.affiliate.api.feign.AffiliateFeignClient;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AffiliateFeignFallback implements AffiliateFeignClient {

	@Override
	public Result<AuthAffiliateDTO> getAffiliateByUsername(String username) {
		log.info("降級");
		return Result.failed(ResultCode.DEGRADATION);
	}

	@Override
	public Result<AffiliateInfoDTO> getAffiliateInfoById(Long id) {
		log.info("降級");
		return Result.failed(ResultCode.DEGRADATION);
	}

	@Override
	public Result<AffiliateInfoDTO> getAffiliateInfoByUsername(String username) {
		log.info("降級");
		return Result.failed(ResultCode.DEGRADATION);
	}

	@Override
	public Result<Boolean> isAffiliateLowLevel(CheckAffiliateLowLevelDTO form) {
		log.info("降級");
		return Result.failed(ResultCode.DEGRADATION);
	}

	@Override
	public Result<Boolean> modifyAffiliateRealName(Long affiliateId, String realName) {
		log.info("降級");
		return Result.failed(ResultCode.DEGRADATION);
	}
}
