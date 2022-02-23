package com.stone.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.ware.entity.PurchaseEntity;
import com.stone.mall.ware.vo.MergerVo;
import com.stone.mall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2022-01-01 00:21:02
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergerVo mergerVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

