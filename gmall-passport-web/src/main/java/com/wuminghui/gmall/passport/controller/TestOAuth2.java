package com.wuminghui.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.wuminghui.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @autor huihui
 * @date 2020/11/9 - 22:02
 */
public class TestOAuth2 {

    public static String getCode() {//111111
        //获得授权码
        //  963302049  http://passport.gmall.com:8085/vlogin
        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=3963302049&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin");
        System.out.println(s1);
        //用户点击授权（用户操作）

        //返回授权码到回调地址
        //code=886546634ff83a2aaa527fbd107cd6c7
//        String s2 = "http://passport.gmall.com:8085/vlogin?code=d67e1ffd26eb53d596491ca47986d4e7";
        return null;

    }
    public static String getAccess_token(){//222222
        //        String s2 = "http://passport.gmall.com:8085/vlogin?code=d67e1ffd26eb53d596491ca47986d4e7";
        return null;

    }
    public static String getUser_token(){//333333
        //通过授权码结合个人登录信息（安全）交换access_token(必须POST请求，保证安全)
        // 963302049   f23646803fc8f215773fbc8e348af542   http://passport.gmall.com:8085/vlogin
        String s3 = "https://api.weibo.com/oauth2/access_token";//client_id=963302049&client_secret=f23646803fc8f215773fbc8e348af542&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=d67e1ffd26eb53d596491ca47986d4e7";
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "3963302049");
        map.put("client_secret", "eb83c0519e25b6a803b218070d7f6c3b");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://passport.gmall.com:8085/vlogin");
        map.put("code", "dc88ec20e44fc81e7f975b89cb831b2d");//授权码在有效期内使用，每新生成一次授权码，说明
        //用户对第三方重新授权，之前的access_token不可用了，重新申请授权码

        String access_token_json = HttpclientUtil.doPost(s3, map);
        Map<String,String> access_map = JSON.parseObject(access_token_json, Map.class);
        System.out.println(access_map.get("access_token"));//  2.00FEgL2GbubN1E32e45b49bdcSQ4BE
        System.out.println(access_map.get("uid"));//     5809883357
        return access_map.get("access_token");

    }
    public static Map<String, String> getUser_info(){//444444
        //用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00FEgL2GbubN1E32e45b49bdcSQ4BE&uid=5809883357";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String, String> user_map = JSON.parseObject(user_json, Map.class);
        System.out.println(user_map.get("XXXX"));
        return user_map;

    }
    public static void main(String[] args) {//静态方法调用的方法必须是静态的
        getUser_info();
    }
}
