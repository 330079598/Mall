package com.stone.mall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.stone.common.constant.WareConstant;
import com.stone.mall.ware.dao.PurchaseDetailDao;
import com.stone.mall.ware.entity.PurchaseDetailEntity;
import com.stone.mall.ware.service.PurchaseDetailService;
import com.stone.mall.ware.vo.MergerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.utils.PageUtils;
import com.stone.common.utils.Query;

import com.stone.mall.ware.dao.PurchaseDao;
import com.stone.mall.ware.entity.PurchaseEntity;
import com.stone.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    PurchaseDetailDao purchaseDetailDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergerVo mergerVo) {
        Long purchaseId = mergerVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            this.save(purchase);
            purchaseId = purchase.getId();
        }
        // TODO：确认采购单的状态是0或者1才可以合并
        PurchaseDetailEntity detailEntity1 = purchaseDetailDao.selectById(purchaseId);
        if (detailEntity1.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                detailEntity1.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
            List<Long> items = mergerVo.getItems();
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(i -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(i);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(collect);
        }


        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);
    }

    @Override
    public void received(List<Long> ids) {
        // 确认当前采购单是新建或者是已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(itme -> {
            if (itme.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode() ||
                    itme.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        // 改变采购单的状态
        if (!collect.isEmpty()) {
            this.updateBatchById(collect);
            // 改变采购项的状态
            collect.forEach((item) -> {
                List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
                List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(entity.getId());
                    detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(detailEntities);
            });
        }


    }
}