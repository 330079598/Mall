package com.stone.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.common.utils.PageUtils;
import com.stone.mall.product.entity.AttrEntity;
import com.stone.mall.product.vo.AttrGroupRelationVo;
import com.stone.mall.product.vo.AttrRespVo;
import com.stone.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-30 23:27:57
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

