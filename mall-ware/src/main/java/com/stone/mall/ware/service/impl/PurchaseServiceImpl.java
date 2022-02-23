package com.stone.mall.ware.service.impl;

import com.stone.common.constant.WareConstant;
import com.stone.mall.ware.dao.PurchaseDetailDao;
import com.stone.mall.ware.entity.PurchaseDetailEntity;
import com.stone.mall.ware.service.PurchaseDetailService;
import com.stone.mall.ware.service.WareSkuService;
import com.stone.mall.ware.vo.MergerVo;
import com.stone.mall.ware.vo.PurchaseDoneVo;
import com.stone.mall.ware.vo.PurchaseItemDoneVo;
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
    @Autowired
    PurchaseDao purchaseDao;
    @Autowired
    WareSkuService wareSkuService;

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
        PurchaseEntity purchase1 = purchaseDao.selectById(purchaseId);
        if (purchase1.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                purchase1.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
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
            item.setStatus(WareConstant.PurchaseDetailStatusEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        // 改变采购单的状态
        if (!collect.isEmpty() || collect.size() > 0) {
            this.updateBatchById(collect);
            // 改变采购项的状态
            collect.forEach((item) -> {
                List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
                List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(entity.getId());
                    detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.RECEIVED.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(detailEntities);
            });
        }


    }

    @Override
    public void done(PurchaseDoneVo doneVo) {
        // 改变采购单状态
        Long id = doneVo.getId();

        // 改变采购项的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            }
            if (flag) {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                // 将充公的采购进行入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        // 改变采购单中台
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode() : WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }
}