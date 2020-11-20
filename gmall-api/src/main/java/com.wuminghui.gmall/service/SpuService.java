package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.PmsProductImage;
import com.wuminghui.gmall.bean.PmsProductInfo;
import com.wuminghui.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/25 - 15:01
 */
public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId);
}
