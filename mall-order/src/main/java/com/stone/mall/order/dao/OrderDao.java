package com.stone.mall.order.dao;

import com.stone.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author stone
 * @email 330079598@qq.com
 * @date 2022-01-01 00:10:17
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
