package com.stone.mall.member.feign;

import com.stone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    // 远程服务地址
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
