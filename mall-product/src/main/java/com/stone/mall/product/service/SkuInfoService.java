package com.stone.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-30 23:27:57
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

