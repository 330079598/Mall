package com.stone.mall.product.feign;

import com.stone.common.to.SkuHasStockVo;
import com.stone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: stone
 * @Title: WareFeignService
 * @date: 2022/2/6 22:39
 * @Description:
 */
@FeignClient("mall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
