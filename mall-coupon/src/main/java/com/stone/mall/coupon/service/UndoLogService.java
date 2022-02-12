package com.stone.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.coupon.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-31 15:46:41
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

