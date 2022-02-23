package com.stone.mall.product.feign;

import com.stone.common.to.SkuReductionTo;
import com.stone.common.to.SpuBoundTo;
import com.stone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: stone
 * @Title: SpuFeignService
 * @date: 2022/1/25 20:35
 * @Description:
 */

@FeignClient("mall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
