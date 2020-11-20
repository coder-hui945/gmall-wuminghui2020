package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/11/4 - 16:04
 */
public interface CartService {
    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);

    List<OmsCartItem> cartList(String userId);

    void checkCart(OmsCartItem omsCartItem);
}
