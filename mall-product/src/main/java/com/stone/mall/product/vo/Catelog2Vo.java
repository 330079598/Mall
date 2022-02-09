package com.stone.mall.product.vo;

import lombok.*;

import java.util.List;

/**
 * @author: stone
 * @Title: Catelog2Vo
 * @date: 2022/2/9 16:38
 * @Description:
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    private String catelog1Id; // 1级父分类id
    private List<Object> catelog3List; // 三级子分类
    private String id;
    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo {
        private String catelog2Id;
        private String id;
        private String name;
    }

}
