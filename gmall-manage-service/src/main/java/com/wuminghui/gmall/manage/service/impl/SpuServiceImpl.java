package com.wuminghui.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wuminghui.gmall.bean.PmsProductImage;
import com.wuminghui.gmall.bean.PmsProductInfo;
import com.wuminghui.gmall.bean.PmsProductSaleAttr;
import com.wuminghui.gmall.bean.PmsProductSaleAttrValue;
import com.wuminghui.gmall.manage.mapper.*;
import com.wuminghui.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/25 - 15:02
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired

    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired

    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired

    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired

    PmsProductImageMapper pmsProductImageMapper;


    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);

        return pmsProductInfos;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {

        //保存SpuInfo

        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        String spuId = pmsProductInfo.getId();

        //保存SPU图片信息

        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();

        for (PmsProductImage spuImage :spuImageList) {

            spuImage.setProductId(spuId);

            pmsProductImageMapper.insert(spuImage);

        }

        //保存SPU销售属性

        PmsProductSaleAttr spuSaleAttr = new PmsProductSaleAttr();

        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();

        for (PmsProductSaleAttr saleAttr : spuSaleAttrList) {

            saleAttr.setProductId(spuId);

            pmsProductSaleAttrMapper.insert(saleAttr);

            //保存SPU销售属性值

            List<PmsProductSaleAttrValue> spuSaleAttrValueList = saleAttr.getSpuSaleAttrValueList();

            for (PmsProductSaleAttrValue attrValue : spuSaleAttrValueList) {

                attrValue.setProductId(spuId);

                pmsProductSaleAttrValueMapper.insert(attrValue);

            }

        }

    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);

        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());//注意销售属性id，不是主键，而是系统字典表中的id
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {

//        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//        pmsProductSaleAttr.setProductId(productId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
//        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
//            String saleAttrId = productSaleAttr.getSaleAttrId();
//            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//            pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
//            pmsProductSaleAttrValue.setProductId(productId);
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//
//            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//        }
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
        return pmsProductSaleAttrs;
    }
}
