package com.c88.affiliate.controller.h5;

import com.alibaba.fastjson.JSONObject;
import com.c88.affiliate.pojo.entity.Affiliate;
import com.c88.affiliate.service.IAffiliateService;
import com.c88.common.core.result.Result;
import com.c88.common.core.result.ResultCode;
import com.c88.common.core.util.HttpUtil;
import com.c88.common.web.exception.BizException;
import com.c88.common.web.util.QRCodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "推廣鏈接")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/h5/affiliate/promotionlink")
public class PromotionLinkController {

    @Value("${agent.promotion.link:http://abc.com}")
    String agentUrl;

    private final IAffiliateService iAffiliateService;


    @Operation(summary = "產生二維碼")
    @GetMapping("/qrcode/{username}")
    public void getAffiliateByUsername(@PathVariable String username, HttpServletResponse response) {
        Affiliate affiliate = iAffiliateService.lambdaQuery().eq(Affiliate::getUsername, username).oneOpt().orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
        String url = agentUrl + "/register?code="+affiliate.getPromotionCode();
        try (OutputStream os = response.getOutputStream()) {
            // 生成二维码对象
            BufferedImage qrCode = QRCodeUtil.getQRCode(url);
            //设置response
            response.setContentType("image/png");
            // 输出jpg格式图片
            ImageIO.write(qrCode, "jpg", os);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("二维码生成失败!");
        }
    }

    @Operation(summary = "代理鏈接")
    @GetMapping("/link/{username}")
    public Result<JSONObject> getLink(@PathVariable String username) {
        Affiliate affiliate = iAffiliateService.lambdaQuery().eq(Affiliate::getUsername, username).oneOpt().orElseThrow(() -> new BizException(ResultCode.USER_NOT_EXIST));
        String longLink = agentUrl + "/register?code="+affiliate.getPromotionCode();

        String shortUrl = iAffiliateService.getAffiliateShortUrl(longLink);

        JSONObject returnObj = new JSONObject();
        returnObj.put("longLink", longLink);
        returnObj.put("shortLink", shortUrl);
        return Result.success(returnObj);
    }
}
