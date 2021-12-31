package com.stone.mall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stone.mall.product.entity.BrandEntity;
import com.stone.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MallProductApplicationTests {


    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setDescript("iphone 13");
//        brandEntity.setName("apple");
//        brandEntity.setLogo("\uD83C\uDF4E");
//        brandService.save(brandEntity);
//        System.out.println("保存成功。。。");
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));

        list.forEach((item) -> {
            System.out.println(item);
        });
    }

}
