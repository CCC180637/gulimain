package com.ccc.gulimall.product.generator;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ccc.gulimall.product.dao.AttrGroupDao;
import com.ccc.gulimall.product.dao.SkuSaleAttrValueDao;
import com.ccc.gulimall.product.entity.BrandEntity;
import com.ccc.gulimall.product.service.BrandService;
import com.ccc.gulimall.product.vo.SkuItemSaleAttrVo;
import com.ccc.gulimall.product.vo.SkuItemVo;
import com.ccc.gulimall.product.vo.SpuItemAttrGroupVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GulimainProductApplicationTests {

    @Autowired
    BrandService brandService;


    @Autowired
    OSSClient ossClient;


    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void SaleTest(){
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(9L);
        System.out.println(saleAttrsBySpuId);
    }


    @Test
    public void  test1(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(9L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }




    @Test
    public void testUpload() throws FileNotFoundException {
     /*   // yourEndpoint填写Bucket所在地域对应的Endpoint。
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId ="LTAI5tD5QrQopZcDJvTNTmh9";
        String accessKeySecret ="IxpQ8JHFJ46njIs8Q3XooP2pR2w7Z6";

        // 创建ClientConfiguration实例，您可以根据实际情况修改默认参数。
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        // 关闭CNAME选项。
        conf.setSupportCname(false);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);*/

        //上传文件流
         FileInputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Pictures\\Saved Pictures\\5c7c18d545d75bf91ebac8163e796e06_t.gif");
         ossClient.putObject("ccc-gulimall","tb2.jpg",inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传成功。。。。。");
    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        /*brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功、、、、");
*/
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 2L));
        list.forEach(user -> System.out.println(user));
    }

    @Test
    public void test() {
        System.out.println(111);
    }


    @Test
    public void testTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("one","hello world"+ UUID.randomUUID().toString());

        String one = ops.get("one");
        System.out.println("之前保存的数据是："+one);
    }


}
