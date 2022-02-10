package com.stone.mall.product.vo;

import lombok.*;

import java.util.List;

/**
 * @author: stone
 * @Title: Catelog2Vo
 * @date: 2022/2/9 16:38
 * @Description:
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private String catalog1Id; // 1级父分类id
    private List<Catelog3Vo> catalog3List; // 三级子分类
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }

}
