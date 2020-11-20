package com.wuminghui.gmall.payment.cotroller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.wuminghui.gmall.annotations.LoginRequired;
import com.wuminghui.gmall.bean.OmsOrder;
import com.wuminghui.gmall.bean.PaymentInfo;
import com.wuminghui.gmall.payment.config.AlipayConfig;
import com.wuminghui.gmall.service.OrderService;
import com.wuminghui.gmall.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Controller
@CrossOrigin
public class paymentController {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;

    @Reference
    OrderService orderService;

    @RequestMapping("alipay/callback/return")
//    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    public String alipayCallBackReturn(HttpServletRequest request, ModelMap modelMap) {
        /*回调请求中获取支付宝参数 ， 详情请参考蚂蚁金服的开发者文档alipay*/
        String sign = request.getParameter("sign");/*支付宝签名*/
        String tradeNo = request.getParameter("trade_no");/*支付宝交易凭证号*/
        String out_trade_no = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");/*订单状态*/
        String totalAmount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String callback_content = request.getQueryString();
        /*通过支付宝的paramMap进行签名验证 ，2.0版本的接口将paramMap参数去掉了，所以我们这里进行一次假验证*/
        if (StringUtils.isNotBlank(sign)) {
            /*验证成功，前做更新之前做幂等性检查*/
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(out_trade_no);
            paymentInfo.setPaymentStatus("已付款");
            paymentInfo.setAlipayTradeNo(tradeNo);
            paymentInfo.setCallbackContent(callback_content);
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setConfirmTime(new Date());
            /*=====================================================================================================================================*/
            /*更新用户的支付状态*/
            /*支付成功后 引起的系统服务 ，——> 订单服务更新 ，——> 库存服务 ，——> 物流服务*/
            /*这里我们将引入分布式事务消息中间件ActiveMQ*/
            /*调用ActiveMQ发送支付成功的消息*/
            /*进行支付更新的幂等性检查操作在updatePayment方法里面，防止与PaymentServiceMqListener一起重复更新*/
            /*=====================================================================================================================================*/
            paymentService.updatePayment(paymentInfo);
        }
        return "finish";
    }

    /*============================================================================================================================================*/
    /*未使用微信支付业务*/
    /*============================================================================================================================================*/
    @RequestMapping("weixin/submit")
    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    public String weixin(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        return null;
    }

    @RequestMapping("alipay/submit")
    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        /*获得一个支付宝的请求客户端（是一个封装好的Http的表单请求）*/
        String form = null;
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();/*创建API对应的request*/

        /*回调函数地址*/
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no", outTradeNo);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", 100);
        map.put("subject", "西南交大定制手机");
        String param = JSON.toJSONString(map);
        alipayRequest.setBizContent(param);
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        /*生成并且保存用户的支付信息*/
        OmsOrder omsOrder = orderService.getOrderByOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setSubject("东方商城商品");
        paymentInfo.setTotalAmount(totalAmount);
        paymentService.savePaymentInfo(paymentInfo);
        /*=================================================================================================================*/
        /*向消息中间件发送一个检查支付状态的延迟消息队列 ，在activeMQ的config文件<broker>标签配置schedulerSupport="true"*/
        /*=================================================================================================================*/
        /*定义监听循环次数6次*/
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNo, 5);
        /*提交请求到支付宝*/
        return form;
    }

    @RequestMapping("index")
    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        modelMap.put("outTradeNo", outTradeNo);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("nickname", nickname);
        return "index";
    }

}
