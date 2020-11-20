package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.PaymentInfo;

import java.util.Map;

/**
 * @autor huihui
 * @date 2020/11/13 - 13:48
 */
public interface PaymentService {
    void savePaymentInfo(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendDelayPaymentResultCheckQueue(String outTradeNo, int count);

    Map<String,Object> checkAlipayPayment(String out_trade_no);
}
