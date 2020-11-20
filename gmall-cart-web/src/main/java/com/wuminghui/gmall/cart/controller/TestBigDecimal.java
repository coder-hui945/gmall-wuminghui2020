package com.wuminghui.gmall.cart.controller;

import java.math.BigDecimal;

/**
 * @autor huihui
 * @date 2020/11/7 - 12:28
 */
public class TestBigDecimal {
    public static void main(String[] args) {
        //初始化
        BigDecimal b1 = new BigDecimal(0.01f);
        BigDecimal b2 = new BigDecimal(0.01d);
        BigDecimal b3 = new BigDecimal("0.01");//推荐，无精度丧失。
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);

        //比较
        System.out.println(b1.compareTo(b2));//1  0 -1


        //运算
        System.out.println(b1.add(b2));
        BigDecimal res = b2.subtract(b1);
        System.out.println("res1: "+res);
        BigDecimal res2 = res.setScale(3, BigDecimal.ROUND_HALF_DOWN);
        System.out.println("res2: "+res2);


        BigDecimal b4 = new BigDecimal("6.2");
        BigDecimal b5 = new BigDecimal("5.5");
        System.out.println(b4.multiply(b5));

        System.out.println(b4.divide(b5, 3, BigDecimal.ROUND_HALF_DOWN));


        //约数
    }
}
