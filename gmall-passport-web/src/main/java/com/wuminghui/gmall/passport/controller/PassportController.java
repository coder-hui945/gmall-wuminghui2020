package com.wuminghui.gmall.passport.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wuminghui.gmall.bean.UmsMember;
import com.wuminghui.gmall.service.UserService;
import com.wuminghui.gmall.util.HttpclientUtil;
import com.wuminghui.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
/**
 * @autor huihui
 * @date 2020/11/7 - 14:15
 */
@Controller
public class PassportController {

    @Reference
    UserService userService;

    @RequestMapping("vlogin")
    public String vlogin(String code, HttpServletRequest request) {
        //授权码换取access_token
        String s3 = "https://api.weibo.com/oauth2/access_token";//client_id=963302049&client_secret=f23646803fc8f215773fbc8e348af542&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=d67e1ffd26eb53d596491ca47986d4e7";
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "3963302049");
        map.put("client_secret", "eb83c0519e25b6a803b218070d7f6c3b");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://passport.gmall.com:8085/vlogin");
        map.put("code", code);//授权码只能在有效期内使用
        String access_token_json = HttpclientUtil.doPost(s3, map);
        Map<String, Object> access_map = JSON.parseObject(access_token_json, Map.class);

        //access_token换取用户信息
        String uid = (String) access_map.get("uid");
        String access_token = (String) access_map.get("access_token");

        String show_user_url = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;
        String user_json = HttpclientUtil.doGet(show_user_url);
        Map<String, Object> user_map = JSON.parseObject(user_json, Map.class);

        //将有用用户信息保存数据库，用户类型设置为微博用户
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid((String) user_map.get("idstr"));
        umsMember.setCity((String) user_map.get("location"));
        umsMember.setNickname((String) user_map.get("screen_name"));
        //微博的性别为m男，f女，n未知，本站性别01表示
        String g = "0";
        String gender = (String) user_map.get("gender");
        if (gender.equals("m"))
            g = "1";

        umsMember.setGender(g);
        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberCheck = userService.checkOauthUser(umsCheck);//检查该用户（社交用户）以前是否登录过

        if (umsMemberCheck == null)
           umsMember =  userService.addOauthUser(umsMember);
        else
            umsMember = umsMemberCheck;

        //生成jwt的token，并且重定向到首页，并携带该token
        String token = "";
        String memberId = umsMember.getId();//rpc的主键返回策略失效，出现null
        String nickname = umsMember.getNickname();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("memberId", memberId);//是保存数据库后主键返回策略生成的id，由于service和controller分离，失效，为null
        userMap.put("nickname", nickname);
        // request.getRemoteAddr();//通过负载均衡后的访问不一定是真实的访问的客户端ip地址，可能是nginx的ip


        String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();//如果没有通过负载均衡nginx，ip为空，通过request直接获取ip。
        }
        if (StringUtils.isBlank(ip)) {//如果以上两种ip都为空，可能为非法访问或负载均衡出错等原因。
            ip = "127.0.0.1";//方便测试
        }
        //此处参数应该按照设计的算法进行加密后生成token，此处只是简单起见，未加密，直接字符
        token = JwtUtil.encode("2020gmallwuminghui", userMap, ip);

        //将token存入redis一份
        userService.addUserToken(token, memberId);

        return "redirect:http://search.gmall.com:8083/index?token="+token;
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {//当前的request来自于authintercepter，request所包含的ip地址是拦截器所在服务器的地址，而不是用户的IP地址。
        //通过JWT校验token真假
        Map<String, String> map = new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token, "2020gmallwuminghui", currentIp);
        if (decode != null) {

            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token = "";
        //调用用户服务验证用户和密码
        UmsMember umsMemberLogin = userService.login(umsMember);
        if (umsMemberLogin != null) {
            //登录成功

            //用JWT制作token
            String memberId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("memberId", memberId);
            userMap.put("nickname", nickname);
            // request.getRemoteAddr();//通过负载均衡后的访问不一定是真实的访问的客户端ip地址，可能是nginx的ip
            String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();//如果没有通过负载均衡nginx，ip为空，通过request直接获取ip。
            }
            if (StringUtils.isBlank(ip)) {//如果以上两种ip都为空，可能为非法访问或负载均衡出错等原因。
                ip = "127.0.0.1";//方便测试
            }
            //此处参数应该按照设计的算法进行加密后生成token，此处只是简单起见，未加密，直接字符
            token = JwtUtil.encode("2020gmallwuminghui", userMap, ip);

            //将token存入redis一份
            userService.addUserToken(token, memberId);

        } else {
            //登录失败
            token = "fail";
        }

        return token;
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap map) {

        map.put("ReturnUrl", ReturnUrl);
        return "index";
    }
}
