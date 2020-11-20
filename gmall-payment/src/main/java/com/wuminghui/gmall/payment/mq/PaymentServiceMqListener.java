package com.wuminghui.gmall.payment.mq;
import com.wuminghui.gmall.bean.PaymentInfo;
import com.wuminghui.gmall.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

/**
 * @autor huihui
 * @date 2020/11/15 - 11:41
 */
@Component
public class PaymentServiceMqListener {

    @Autowired
    PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
    public void consumePaymenChecktResult(MapMessage mapMessage) throws JMSException {
        String out_trade_no = mapMessage.getString("out_trade_no");
        Integer count = 0;
        if (mapMessage.getString("count") != null){
            count = Integer.parseInt("" + mapMessage.getInt("count"));
        }
        /*==================================================================================================================================*/
        /*调用阿里的蚂蚁金服的支付查询接口，在PaymentServiceImpl实现*/
        /*==================================================================================================================================*/

        //调用paymentservice的支付宝检查接口
        System.out.println("调用支付接口，进行验证检查。");
        Map<String, Object> resultmap = paymentService.checkAlipayPayment(out_trade_no);

        if (resultmap==null||resultmap.isEmpty()) {
            /*继续发送延时消息检查用户支付状态*/

            if (count > 0){
                count--;
                System.out.println("检查次数剩余"+count);
                paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
            }else {
                System.out.println("支付失败，检查次数用尽。");
                return ;
            }

        } else {
            String trade_status = (String) resultmap.get("trade_status");

            //根据查询的状态结果，判断是否进行下次延迟任务还是支付成功更新数据和后续任务
            if (StringUtils.isNotBlank(trade_status)&&trade_status.equals("TRADE_SUCCESS")) {
                //支付成功，更新支付和发送支付队列(更新之前做幂等性检查)
                System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列。");
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(out_trade_no);
                paymentInfo.setPaymentStatus("已付款");
                paymentInfo.setAlipayTradeNo((String) resultmap.get("tradeNo"));
                paymentInfo.setCallbackContent((String) resultmap.get("callback_content"));
                paymentInfo.setCallbackTime(new Date());
                paymentInfo.setConfirmTime(new Date());
                paymentService.updatePayment(paymentInfo);

            } else {
                /*继续发送延时消息检查用户支付状态*/
                if (count > 0){
                    count--;
                    System.out.println("检查次数剩余"+count);
                    paymentService.sendDelayPaymentResultCheckQueue(out_trade_no, count);
                }else {
                    System.out.println("支付失败,检查次数用尽。");
                    return ;
                }
            }
        }
    }
}
