package com.stone.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: stone
 * @Title: MergerVo
 * @date: 2022/1/31 10:25
 * @Description:
 */
@Data
public class MergerVo {
    // purchaseId: 1, //整单id
    //  items:[1,2,3,4] //合并项集合
    private Long purchaseId;
    private List<Long> items;
}
