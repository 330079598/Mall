package com.stone.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-31 15:46:41
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

