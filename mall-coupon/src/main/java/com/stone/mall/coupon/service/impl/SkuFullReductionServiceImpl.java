package com.stone.mall.coupon.service.impl;

import com.stone.common.to.MemberPrice;
import com.stone.common.to.SkuReductionTo;
import com.stone.mall.coupon.entity.MemberPriceEntity;
import com.stone.mall.coupon.entity.SkuLadderEntity;
import com.stone.mall.coupon.service.MemberPriceService;
import com.stone.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.utils.PageUtils;
import com.stone.common.utils.Query;

import com.stone.mall.coupon.dao.SkuFullReductionDao;
import com.stone.mall.coupon.entity.SkuFullReductionEntity;
import com.stone.mall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo reductionTo) {
        // 保存满减打折，会员价
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(reductionTo.getSkuId());
        skuLadderEntity.setFullCount(reductionTo.getFullCount());
        skuLadderEntity.setDiscount(reductionTo.getDiscount());
        skuLadderEntity.setAddOther(reductionTo.getCountStatus());
        skuLadderService.save(skuLadderEntity);

        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(reductionTo, skuFullReduction);
        this.save(skuFullReduction);

        List<MemberPrice> memberPriceList = reductionTo.getMemberPriceList();

        List<MemberPriceEntity> collect = memberPriceList.stream().map(item -> {
            MemberPriceEntity priceEntity = new MemberPriceEntity();
            priceEntity.setSkuId(reductionTo.getSkuId());
            priceEntity.setMemberLevelId(item.getId());
            priceEntity.setMemberLevelName(item.getName());
            priceEntity.setMemberPrice(item.getPrice());
            priceEntity.setAddOther(1);
            return priceEntity;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);

    }

}