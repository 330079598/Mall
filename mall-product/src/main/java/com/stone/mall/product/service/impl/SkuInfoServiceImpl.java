package com.stone.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.utils.PageUtils;
import com.stone.common.utils.Query;
import com.stone.mall.product.dao.SkuInfoDao;
import com.stone.mall.product.entity.SkuImagesEntity;
import com.stone.mall.product.entity.SkuInfoEntity;
import com.stone.mall.product.entity.SpuInfoDescEntity;
import com.stone.mall.product.service.AttrGroupService;
import com.stone.mall.product.service.SkuImagesService;
import com.stone.mall.product.service.SkuInfoService;
import com.stone.mall.product.service.SpuInfoDescService;
import com.stone.mall.product.vo.SkuItemVo;
import com.stone.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

	@Autowired
	SkuImagesService skuImagesService;
	@Autowired
	SpuInfoDescService spuInfoDescService;
	@Autowired
	AttrGroupService attrGroupService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				new QueryWrapper<SkuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
		this.baseMapper.insert(skuInfoEntity);
	}

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params) {
		QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
		/**
		 * key: '华为',//检索关键字
		 * catelogId: 0,
		 * brandId: 0,
		 * min: 0,
		 * max: 0
		 */
		String key = (String) params.get("key");
		if (StringUtils.isNotEmpty(key)) {
			wrapper.and(w -> {
				w.eq("sku_id", key).or().like("sku_name", key);
			});
		}
		String catelogId = (String) params.get("catelogId");
		if (StringUtils.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
			wrapper.eq("catalog_id", catelogId);
		}

		String brandId = (String) params.get("brandId");
		if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}
		String min = (String) params.get("min");
		if (StringUtils.isNotEmpty(min)) {
			wrapper.ge("price", min);
		}

		String max = (String) params.get("max");
		if (StringUtils.isNotEmpty(max)) {
			try {
				BigDecimal bigDecimal = new BigDecimal(max);
				if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
					wrapper.le("price", max);
				}
			} catch (Exception e) {
			}
		}

		IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
		return new PageUtils(page);
	}

	@Override
	public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
		List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
		return list;
	}

	@Override
	public SkuItemVo item(Long skuId) {
		SkuItemVo skuItemVo = new SkuItemVo();
		// sku 基本信息获取 pms_sku_info
		SkuInfoEntity info = getById(skuId);
		skuItemVo.setInfo(info);
		Long spuId = info.getSpuId();
		Long catalogId = info.getCatalogId();

		// sku 的图片信息 pms_sku_images
		List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
		// 获取spu的销售属性组合

		// 获取spu的介绍

		SpuInfoDescEntity descEntity = spuInfoDescService.getById(spuId);
		skuItemVo.setDesc(descEntity);

		// 获取spu的规格参数信息
		List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
		skuItemVo.setGroupAttrs(attrGroupVos);


		return skuItemVo;
	}

}