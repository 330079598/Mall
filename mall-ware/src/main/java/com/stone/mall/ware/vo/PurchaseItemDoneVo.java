package com.stone.mall.ware.vo;

import lombok.Data;

/**
 * @author: stone
 * @Title: PurchaseItemDoneVo
 * @date: 2022/2/3 11:03
 * @Description:
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
