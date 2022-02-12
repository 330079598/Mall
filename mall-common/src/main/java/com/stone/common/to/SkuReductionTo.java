package com.stone.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: stone
 * @Title: SkuReductionTo
 * @date: 2022/1/27 15:25
 * @Description:
 */

@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
