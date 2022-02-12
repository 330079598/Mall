package com.stone.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: stone
 * @Title: SpuBoundTo
 * @date: 2022/1/25 20:46
 * @Description:
 */

@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBound;
}
