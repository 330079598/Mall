package com.stone.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.product.entity.SpuInfoDescEntity;
import com.stone.mall.product.entity.SpuInfoEntity;
import com.stone.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-30 23:27:57
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBatchSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

