package com.stone.mall.search.service;

import com.stone.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author: stone
 * @Title: ProductSaveService
 * @date: 2022/2/7 11:20
 * @Description:
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
