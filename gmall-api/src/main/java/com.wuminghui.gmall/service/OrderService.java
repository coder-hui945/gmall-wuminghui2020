package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.OmsOrder;

import java.math.BigDecimal; /**
 * @autor huihui
 * @date 2020/11/11 - 21:36
 */
public interface OrderService {
    String checkTradeCode(String memberId,String tradeCode);

    String genTradeCode(String memberId);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);
}
