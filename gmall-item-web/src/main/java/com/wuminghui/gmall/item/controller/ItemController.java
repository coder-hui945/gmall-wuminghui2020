package com.wuminghui.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wuminghui.gmall.bean.PmsProductSaleAttr;
import com.wuminghui.gmall.bean.PmsSkuInfo;
import com.wuminghui.gmall.bean.PmsSkuSaleAttrValue;
import com.wuminghui.gmall.service.SkuService;
import com.wuminghui.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor huihui
 * @date 2020/10/28 - 9:09
 */
@Controller

public class ItemController {
    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map , HttpServletRequest request) {
        /*1.没有用nginx负载均衡的算法，获取用户的IP地址*/
        String ip = request.getRemoteAddr();
        /*2.使用nginx负载均衡的算法，获取用户的IP地址*/
        String header = request.getHeader("");

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId , ip);
        /*sku对象*/
        map.put("skuInfo", pmsSkuInfo);
        /*销售属性列表 pmsSkuInfo.getProductId()*/
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), pmsSkuInfo.getId());
        map.put("skuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        /*查询当前的sku的spu的其他sku集合的hash表*/
        Map<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String hashKey = "";
            String hashValue = skuInfo.getId();
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValues) {
                hashKey += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";//5|7|9...
            }
            skuSaleAttrHash.put(hashKey, hashValue);
        }
        /*将sku的销售属性hash表放到页面*/
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        map.put("skuSaleAttrHashJsonStr", skuSaleAttrHashJsonStr);

        return "item";
    }

//    测试方法
    @RequestMapping("index")
    public String index(ModelMap modelMap){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("循环数据" + i);
        }
        modelMap.put("hello","hello hahaahahh");
        modelMap.put("list",list);
        modelMap.put("check","1");
        return "index";

    }
}
