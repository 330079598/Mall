package com.stone.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * ้่ดงๅๅ 
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2022-01-01 00:10:17
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

