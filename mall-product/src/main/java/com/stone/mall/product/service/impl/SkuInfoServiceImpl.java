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
import com.stone.mall.product.service.*;
import com.stone.mall.product.vo.ItemSaleAttrVo;
import com.stone.mall.product.vo.SkuItemVo;
import com.stone.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

	@Autowired
	SkuImagesService skuImagesService;
	@Autowired
	SpuInfoDescService spuInfoDescService;
	@Autowired
	AttrGroupService attrGroupService;
	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;
	@Autowired
	private ThreadPoolExecutor executor;

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


	/**
	 * @Author: stone
	 * @Description: 查询页面详细内容
	 **/
	@Override
	public SkuItemVo item(Long skuId) {
		SkuItemVo skuItemVo = new SkuItemVo();

		CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
			// 1. sku 基本信息获取 pms_sku_info
			SkuInfoEntity info = getById(skuId);
			skuItemVo.setInfo(info);
			return info;
		}, executor);

		// 无需获取返回值
		CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
			// 2. sku图片信息
			List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
			skuItemVo.setImages(images);
		}, executor);

		// 在1之后
		CompletableFuture<Void> spuAttrFuture = infoFuture.thenAcceptAsync(res -> {
			// 3. 获取spu销售属性组合list
			List<ItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBuSpuId(res.getSpuId());
			skuItemVo.setSaleAttr(saleAttrVos);
		}, executor);

		// 在1之后
		CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync(res -> {
			// 4. 获取spu介绍
			SpuInfoDescEntity spuInfo = spuInfoDescService.getById(res.getSpuId());
			skuItemVo.setDesc(spuInfo);
		}, executor);

		// 在1之后
		CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync(res -> {
			// 5. 获取spu规格参数信息
			List<SpuItemAttrGroupVo> group = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
			skuItemVo.setGroupAttrs(group);
		}, executor);

		// 等待所有任务完成再返回
		CompletableFuture.allOf(imageFuture, spuAttrFuture, descFuture, baseAttrFuture);

		return skuItemVo;
	}

}