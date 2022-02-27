package com.stone.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-30 23:27:57
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

	List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}

