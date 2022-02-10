package com.stone.mall.product;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stone.mall.product.entity.BrandEntity;
import com.stone.mall.product.service.BrandService;
import com.stone.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class MallProductApplicationTests {


    @Autowired
    BrandService brandService;
//    @Autowired
//    OSSClient ossClient;
    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","word_" + UUID.randomUUID().toString());
        String hello = ops.get("hello");
        System.out.println("保存的数据是:" + hello);
    }
    
    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

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


    @Test
    public void Upload() throws FileNotFoundException {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5t6McHdFv4UQHjx7R8ZG";
        String accessKeySecret = "b7GJznsO7kksmZzYWuqWbcGKpcYISw";
        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "stone-mall-oss";

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        InputStream inputStream = new FileInputStream("C:\\Users\\tao19\\Desktop\\iphone11.jpg");
        ossClient.putObject("stone-mall-oss", "iphone11.jpg", inputStream);
        ossClient.shutdown();
        System.out.println("上传完成。。。。");
    }

//    @Test
//    public void testUpload() throws FileNotFoundException {
//        InputStream inputStream = new FileInputStream("C:\\Users\\tao19\\Desktop\\iphone11.jpg");
//        ossClient.putObject("stone-mall-oss", "iphone11.jpg", inputStream);
//        ossClient.shutdown();
//        System.out.println("上传完成。。。。");
//    }


}