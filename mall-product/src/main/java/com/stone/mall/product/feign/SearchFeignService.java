package com.stone.mall.product.feign;

import com.stone.common.to.es.SkuEsModel;
import com.stone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: stone
 * @Title: SearchFeignService
 * @date: 2022/2/7 17:23
 * @Description:
 */
@FeignClient("mall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList);
}
