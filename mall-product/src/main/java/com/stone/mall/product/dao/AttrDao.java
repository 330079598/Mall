package com.stone.mall.product.dao;

import com.stone.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-30 23:27:57
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrId(@Param("attrIds") List<Long> attrIds);
}
