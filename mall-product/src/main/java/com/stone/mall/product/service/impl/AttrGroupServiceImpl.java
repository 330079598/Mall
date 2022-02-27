package com.stone.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.utils.PageUtils;
import com.stone.common.utils.Query;
import com.stone.mall.product.dao.AttrGroupDao;
import com.stone.mall.product.entity.AttrEntity;
import com.stone.mall.product.entity.AttrGroupEntity;
import com.stone.mall.product.service.AttrGroupService;
import com.stone.mall.product.service.AttrService;
import com.stone.mall.product.vo.AttrGroupWithAttrsVo;
import com.stone.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Autowired
	AttrService attrService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrGroupEntity> page = this.page(
				new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<AttrGroupEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
		String key = (String) params.get("key");
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
		if (StringUtils.isNotEmpty(key)) {
			wrapper.and((obj) -> {
				obj.eq("attr_group_id", key).or().like("attr_group_name", key);
			});
		}
		if (catelogId == 0) {
			IPage<AttrGroupEntity> page = this.page(
					new Query<AttrGroupEntity>().getPage(params),
					wrapper
			);
			return new PageUtils(page);
		} else {
			wrapper.eq("catelog_id", catelogId);
			IPage<AttrGroupEntity> page = this.page(
					new Query<AttrGroupEntity>().getPage(params),
					wrapper);
			return new PageUtils(page);
		}

	}


	/**
	 * 根据分类id查出所有分组以及这些组里面的属性
	 *
	 * @param catelogId
	 * @return
	 */
	@Override
	public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
		// 查询分组信息
		List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

		// 查询所有属性
		List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
			AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
			BeanUtils.copyProperties(group, attrsVo);
			List<AttrEntity> relationAttr = attrService.getRelationAttr(attrsVo.getAttrGroupId());
			attrsVo.setAttrs(relationAttr);
			return attrsVo;
		}).collect(Collectors.toList());
		return collect;
	}

	// 查出当前spu对应的所有属性的分组信息以及
	@Override
	public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
		List<SpuItemAttrGroupVo> vos = getBaseMapper().getAttrGroupWithAttrsBySpuId(spuId, catalogId);
		baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
		return vos;
	}

}