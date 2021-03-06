package com.stone.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.stone.common.constant.ProductConstant;
import com.stone.common.to.SkuHasStockVo;
import com.stone.common.to.SkuReductionTo;
import com.stone.common.to.SpuBoundTo;
import com.stone.common.to.es.SkuEsModel;
import com.stone.common.utils.R;
import com.stone.mall.product.entity.*;
import com.stone.mall.product.feign.CouponFeignService;
import com.stone.mall.product.feign.SearchFeignService;
import com.stone.mall.product.feign.WareFeignService;
import com.stone.mall.product.service.*;
import com.stone.mall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.utils.PageUtils;
import com.stone.common.utils.Query;

import com.stone.mall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Autowired
	SpuInfoDescService spuInfoDescService;
	@Autowired
	SpuImagesService spuImagesService;
	@Autowired
	AttrService attrService;
	@Autowired
	ProductAttrValueService productAttrValueService;
	@Autowired
	SkuInfoService skuInfoService;
	@Autowired
	SkuImagesService skuImagesService;
	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;
	@Autowired
	CouponFeignService couponFeignService;
	@Autowired
	BrandService brandService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	WareFeignService wareFeignService;
	@Autowired
	SearchFeignService searchFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				new QueryWrapper<SpuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveSpuInfo(SpuSaveVo vo) {
		// 1.??????spu???????????? pms_sku_info
		SpuInfoEntity infoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(vo, infoEntity);
		infoEntity.setCreateTime(new Date());
		infoEntity.setUpdateTime(new Date());
		this.saveBatchSpuInfo(infoEntity);

		// 2.??????spu??????????????? pms_spu_info_desc
		List<String> decript = vo.getDecript();
		SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
		descEntity.setSpuId(infoEntity.getId());
		descEntity.setDecript(String.join(",", decript));
		spuInfoDescService.saveSpuInfoDesc(descEntity);

		// 3.??????spu???????????? pms_sku_images
		// ?????????????????????
		List<String> images = vo.getImages();
		// ????????????,????????????????????????spu??????
		spuImagesService.saveImages(infoEntity.getId(), images);

		// 4. ??????spu??????????????? pms_product_attr_value
		List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
		List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
			ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
			BeanUtils.copyProperties(attr, valueEntity);
			AttrEntity byId = attrService.getById(attr.getAttrId());
			valueEntity.setAttrName(byId.getAttrName());
			valueEntity.setAttrValue(attr.getAttrValues());
			valueEntity.setSpuId(infoEntity.getId());
			return valueEntity;
		}).collect(Collectors.toList());
		productAttrValueService.saveProductAttr(collect);

		// spu???????????????
		Bounds bounds = vo.getBounds();
		SpuBoundTo spuBoundTo = new SpuBoundTo();
		BeanUtils.copyProperties(bounds, spuBoundTo);
		spuBoundTo.setSpuId(infoEntity.getId());
		R r = couponFeignService.saveSpuBounds(spuBoundTo);
		if (r.getCode() != 0) {
			log.error("????????????spu?????????????????????");
		}


		// 5.????????????spu???????????????sku??????
		// 5.1 sku??????????????????pms_sku_info
		List<Skus> skus = vo.getSkus();
		if (skus != null && skus.size() > 0) {
			skus.forEach(item -> {
				String defaultImg = "";
				for (Images image : item.getImages()) {
					if (image.getDefaultImg() == 1) {
						defaultImg = image.getImgUrl();
					}
				}
				SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
				BeanUtils.copyProperties(item, skuInfoEntity);
				skuInfoEntity.setBrandId(infoEntity.getBrandId());
				skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
				skuInfoEntity.setSaleCount(0L);
				skuInfoEntity.setSpuId(infoEntity.getId());
				skuInfoEntity.setSkuDefaultImg(defaultImg);
				// sku???????????????
				skuInfoService.saveSkuInfo(skuInfoEntity);
				Long skuId = skuInfoEntity.getSkuId();
				List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
					SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
					return skuImagesEntity;
				}).filter(entiey -> {
					return StringUtils.isNotEmpty(entiey.getImgUrl());
				}).collect(Collectors.toList());
				// 5.2 sku??????????????????pms_sku_images
				skuImagesService.saveBatch(imagesEntities);

				List<Attr> attrs = item.getAttr();
				List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(a -> {
					SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(a, attrValueEntity);
					attrValueEntity.setSkuId(skuId);
					return attrValueEntity;
				}).collect(Collectors.toList());
				// 5.3 sku????????????????????????pms_sku_sale_attr_value
				skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
				// 5.4 sku???????????????????????????
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(item, skuReductionTo);
				skuReductionTo.setSkuId(skuId);
				skuReductionTo.setMemberPrice(item.getMemberPrice());
				if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
					R r0 = couponFeignService.saveSkuReduction(skuReductionTo);
					if (r0.getCode() != 0) {
						log.error("????????????spu?????????????????????");
					}
				}

			});
		}
	}

	@Override
	public void saveBatchSpuInfo(SpuInfoEntity infoEntity) {
		this.baseMapper.insert(infoEntity);
	}

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params) {
		QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
		String key = (String) params.get("key");
		if (StringUtils.isNotEmpty(key)) {
			wrapper.and((w) -> {
				w.eq("id", key).or().like("spu_name", key);
			});
		}

		String status = (String) params.get("status");
		if (StringUtils.isNotEmpty(status)) {
			wrapper.eq("publish_status", status);
		}

		String brandId = (String) params.get("brandId");
		if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}

		String catelogId = (String) params.get("catelogId");
		if (StringUtils.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
			wrapper.eq("catalog_id", catelogId);
		}


		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				wrapper
		);
		return new PageUtils(page);
	}

	/**
	 * ????????????
	 *
	 * @param spuId
	 */
	@Override
	public void up(Long spuId) {
		// 1. ???????????? ????????????spuid?????????????????????
		List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
		// ????????????sku???????????????
		List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
		// 2. ????????????sku?????????

		// 3. ????????????sku????????????????????????????????????
		List<ProductAttrValueEntity> baseAttrs = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
		// ??????????????????id
		List<Long> attrIds = baseAttrs.stream().map(attr -> {
			return attr.getAttrId();
		}).collect(Collectors.toList());
		List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);

		Set<Long> idSet = new HashSet<>(searchAttrIds);
		List<SkuEsModel.Attrs> attrList = baseAttrs.stream().filter(item -> {
			return idSet.contains(item.getAttrId());
		}).map(item -> {
			SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
			BeanUtils.copyProperties(item, attrs);
			attrs.setAttrValue(item.getAttrValue());
			return attrs;
		}).collect(Collectors.toList());

		// ??????????????????,?????????????????????????????????
		Map<Long, Boolean> stockMap = null;
		try {
			R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
			// TODO:??????????????????protect??????
			TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
			};
			stockMap = skuHasStock.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
		} catch (Exception e) {
			log.error("????????????????????????:??????{}", e);
		}

		// ????????????sku?????????
		Map<Long, Boolean> finalStockMap = stockMap;
		List<SkuEsModel> upProducts = skuInfoEntities.stream().map(sku -> {
			// ?????????????????????
			SkuEsModel esModel = new SkuEsModel();
			BeanUtils.copyProperties(sku, esModel);
			esModel.setSkuPrice(sku.getPrice());
			esModel.setSkuImg(sku.getSkuDefaultImg());

			// ??????????????????
			if (finalStockMap == null) {
				esModel.setHasStock(true);
			} else {
				esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
			}


			BrandEntity brand = brandService.getById(sku.getBrandId());
			if (brand != null) {
				esModel.setBrandName(brand.getName());
				esModel.setBrandImg(brand.getLogo());
				esModel.setBrandId(brand.getBrandId());
			}
			CategoryEntity category = categoryService.getById(esModel.getCatalogId());
			esModel.setCatalogName(category.getName());
			// ??????????????????
			esModel.setHotScore(0L);

			// ??????????????????
			esModel.setAttrs(attrList);

			return esModel;
		}).collect(Collectors.toList());

		// TODO:??????????????????es????????????:mall-search
		R r = searchFeignService.productStatusUp(upProducts);
		if (r.getCode() == 0) {
			// ??????????????????
			// ????????????spu??????
			baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
		} else {
			// ??????????????????
			// TODO:????????????,???????????????,????????????
		}
	}


}