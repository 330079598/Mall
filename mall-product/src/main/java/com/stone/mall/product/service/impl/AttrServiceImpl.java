package com.stone.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.constant.ProductConstant;
import com.stone.common.utils.PageUtils;
import com.stone.common.utils.Query;
import com.stone.mall.product.dao.AttrAttrgroupRelationDao;
import com.stone.mall.product.dao.AttrDao;
import com.stone.mall.product.dao.AttrGroupDao;
import com.stone.mall.product.dao.CategoryDao;
import com.stone.mall.product.entity.AttrAttrgroupRelationEntity;
import com.stone.mall.product.entity.AttrEntity;
import com.stone.mall.product.entity.AttrGroupEntity;
import com.stone.mall.product.entity.CategoryEntity;
import com.stone.mall.product.service.AttrService;
import com.stone.mall.product.service.CategoryService;
import com.stone.mall.product.vo.AttrGroupRelationVo;
import com.stone.mall.product.vo.AttrRespVo;
import com.stone.mall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Autowired
	AttrAttrgroupRelationDao relationDao;
	@Autowired
	AttrGroupDao attrGroupDao;
	@Autowired
	CategoryDao categoryDao;
	@Autowired
	CategoryService categoryService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				new QueryWrapper<AttrEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		// ??????????????????
		this.save(attrEntity);

		// ??????????????????
		if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(attr.getAttrGroupId());
			relationEntity.setAttrId(attrEntity.getAttrId());
			relationDao.insert(relationEntity);
		}
	}

	@Override
	public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
		QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type",
				"base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
		if (catelogId != 0) {
			queryWrapper.eq("catelog_id", catelogId);
		}

		String key = (String) params.get("key");
		if (StringUtils.isNotEmpty(key)) {
			queryWrapper.and((wrapper) -> {
				wrapper.eq("attr_id", key).or().like("attr_name", key);
			});
		}
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				queryWrapper
		);

		PageUtils pageUtils = new PageUtils(page);
		List<AttrEntity> records = page.getRecords();
		List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
			AttrRespVo attrRespVo = new AttrRespVo();
			BeanUtils.copyProperties(attrEntity, attrRespVo);

			if ("base".equalsIgnoreCase(type)) {
				AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
				if (attrId != null && attrId.getAttrGroupId() != null) {
					AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
					attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}


			CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
			if (categoryEntity != null) {
				attrRespVo.setCatelogName(categoryEntity.getName());
			}

			return attrRespVo;
		}).collect(Collectors.toList());

		pageUtils.setList(respVos);
		return pageUtils;
	}

	@Cacheable(value = "attr", key = "'attrinfo:+#root.args[0]'")
	@Override
	public AttrRespVo getAttrInfo(Long attrId) {
		AttrRespVo attrRespVo = new AttrRespVo();
		AttrEntity attrEntity = this.getById(attrId);
		BeanUtils.copyProperties(attrEntity, attrRespVo);

		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			// ????????????
			AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
			if (relationEntity != null) {
				attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
				AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
				if (attrGroupEntity != null) {
					attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}
		}

		Long catlogId = attrEntity.getCatelogId();
		Long[] catelogPath = categoryService.findCatelogPath(catlogId);
		attrRespVo.setCatelogPath(catelogPath);
		CategoryEntity categoryEntity = categoryDao.selectById(catlogId);
		if (categoryEntity != null) {
			attrRespVo.setCatelogName(categoryEntity.getName());
		}

		return attrRespVo;
	}

	@Transactional
	@Override
	public void updateAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		this.updateById(attrEntity);

		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			// ??????????????????
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(attr.getAttrGroupId());
			relationEntity.setAttrId(attr.getAttrId());

			Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			if (count > 0) {
				relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			} else {
				relationDao.insert(relationEntity);
			}
		}
	}

	/**
	 * ????????????id?????????????????????????????????
	 *
	 * @param attrgroupId
	 * @return
	 */
	@Override
	public List<AttrEntity> getRelationAttr(Long attrgroupId) {
		List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
		List<Long> attrIds = entities.stream().map((attr) -> {
			return attr.getAttrId();
		}).collect(Collectors.toList());

		if (attrIds == null || attrIds.size() == 0) {
			return null;
		}
		List<AttrEntity> attrEntities = this.listByIds(attrIds);

		return attrEntities;
	}

	@Override
	public void deleteRelation(AttrGroupRelationVo[] vos) {
		List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(item, relationEntity);
			return relationEntity;
		}).collect(Collectors.toList());
		relationDao.deleteBatchRelation(entities);
	}

	/**
	 * ?????????????????????????????????????????????
	 *
	 * @param params
	 * @param attrgroupId
	 * @return
	 */
	@Override
	public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
		// ??????????????????????????????????????????????????????????????????
		AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
		Long catelogId = attrGroupEntity.getCatelogId();

		// ?????????????????????????????????????????????????????????
		//      ??????????????????????????????
		List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
		List<Long> collect = group.stream().map(item -> {
			return item.getAttrGroupId();
		}).collect(Collectors.toList());
		//      ???????????????????????????
		List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
		List<Long> attrIds = groupId.stream().map(itme -> {
			return itme.getAttrId();
		}).collect(Collectors.toList());
		//      ???????????????????????????????????????????????????
		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
		if (attrIds != null && attrIds.size() > 0) {
			wrapper.notIn("attr_id", attrIds);
		}
		// ??????????????????
		String key = (String) params.get("key");
		if (StringUtils.isNotEmpty(key)) {
			wrapper.and((w) -> {
				w.eq("attr_id", key).or().like("attr_name", key);
			});
		}

		IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

		PageUtils pageUtils = new PageUtils(page);

		return pageUtils;
	}

	@Override
	public List<Long> selectSearchAttrs(List<Long> attrIds) {
		return baseMapper.selectSearchAttrId(attrIds);
	}
}