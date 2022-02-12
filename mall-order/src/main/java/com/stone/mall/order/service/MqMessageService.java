package com.stone.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2022-01-01 00:10:17
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

