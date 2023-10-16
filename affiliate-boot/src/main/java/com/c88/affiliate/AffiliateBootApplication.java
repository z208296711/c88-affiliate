package com.c88.affiliate;

import com.c88.feign.AuthFeignClient;
import com.c88.game.adapter.api.PlatformClient;
import com.c88.member.api.MemberFeignClient;
import com.c88.payment.client.AffiliateBalanceChangeRecordClient;
import com.c88.payment.client.MemberRechargeClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan(
        basePackages = {"com.c88.affiliate.mapper"}
)
@EnableFeignClients(basePackageClasses = {MemberFeignClient.class, AuthFeignClient.class, MemberRechargeClient.class, PlatformClient.class, AffiliateBalanceChangeRecordClient.class})
@SpringBootApplication
public class AffiliateBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AffiliateBootApplication.class, args);
    }

}
