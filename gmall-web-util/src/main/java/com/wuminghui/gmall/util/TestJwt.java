package com.wuminghui.gmall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor huihui
 * @date 2020/11/8 - 13:28
 */
public class TestJwt {
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","wuminghui");
        map.put("age","25");
        String ip = "127.0.0.1";
        String time = new SimpleDateFormat("yyyyMMdd HHmm").format(new Date());
        String encode = JwtUtil.encode("2020gmallwuminghui", map, ip + time);
        System.out.println(encode);
    }
}
