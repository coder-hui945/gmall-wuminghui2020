package com.wuminghui.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.wuminghui.gmall.annotations.LoginRequired;
import com.wuminghui.gmall.util.CookieUtil;
import com.wuminghui.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {//Authorization：批准


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //拦截代码
        //判断被拦截的请求访问的方法的注解（是否是需要拦截的）
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        //通过注解判断是否需要拦截
        if (methodAnnotation == null) {
            return true;
        }

        //校验token之前获取token
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken))
            token = oldToken;
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken))
            token = newToken;


        //拦截成功，判断是否必须登录
        boolean loginSuccess = methodAnnotation.loginSuccess();//获取该请求是否必须登录成功

        //统一验证：调用认证中心进行验证，远程调用。
        String success = "fail";
        Map<String,String> successMap = new HashMap<>();
        if (StringUtils.isNotBlank(token)) {
            String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();//如果没有通过负载均衡nginx，ip为空，通过request直接获取ip。
            }
            if (StringUtils.isBlank(ip)) {//如果以上两种ip都为空，可能为非法访问或负载均衡出错等原因。
                ip = "127.0.0.1";//方便测试
            }
            String successJson = HttpclientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token+"&currentIp="+ip);
            successMap = JSON.parseObject(successJson, Map.class);
            success = successMap.get("status");

        }

        if (loginSuccess) {
            //必须登录才能使用
            if (!success.equals("success")) {
                //验证不通过，重定向回passport登录
                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://passport.gmall.com:8085/index?ReturnUrl=" + requestURL);
                return false;
            }
            //验证通过，覆盖cookie中的token
            //将token携带的用户信息写入
            request.setAttribute("memberId", successMap.get("memberId"));
            request.setAttribute("nickname", successMap.get("nickname"));

            //向cookie更新，验证通过，覆盖cookie中的token
            if (StringUtils.isNotBlank(token))
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);


        } else {
            //可以不登陆也能用，但是必须验证
            if (success.equals("success")) {
                //将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));

                //向cookie更新，验证通过，覆盖cookie中的token
                if (StringUtils.isNotBlank(token))
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);

            }
        }


        return true;
    }
}