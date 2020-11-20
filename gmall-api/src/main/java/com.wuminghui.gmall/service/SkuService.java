package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/27 - 12:29
 */
public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    List<PmsSkuInfo> getAllSku(String catalog3Id);

    PmsSkuInfo getSkuById(String skuId, String ip);

    boolean checkPrice(String productSkuId, BigDecimal productPrice);
}
