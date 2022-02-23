package com.stone.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: stone
 * @Title: PurchaseDoneVo
 * @date: 2022/2/3 10:43
 * @Description:
 */

@Data
public class PurchaseDoneVo {

    @NotNull
    private Long id;
    private List<PurchaseItemDoneVo> items;
}
