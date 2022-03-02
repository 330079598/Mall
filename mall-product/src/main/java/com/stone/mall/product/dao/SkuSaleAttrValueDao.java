package com.stone.mall.product.dao;

import com.stone.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stone.mall.product.vo.ItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-30 23:27:57
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

	List<ItemSaleAttrVo> getSaleAttrsBuSpuId(@Param("spuId") Long spuId);
}
