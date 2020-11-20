package com.wuminghui.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wuminghui.gmall.bean.UmsMember;
import com.wuminghui.gmall.bean.UmsMemberReceiveAddress;
import com.wuminghui.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/23 - 16:49
 */
@Controller
public class UserController {
    @Reference
    UserService userService;

    @ResponseBody
    @RequestMapping("getReceiveAddressByMemberId")
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){

        List<UmsMemberReceiveAddress> umsMemberReceiveAddress = userService.getReceiveAddressByMemberId(memberId);

        return umsMemberReceiveAddress;
    }

    @ResponseBody
    @RequestMapping("getAllUser")
    public List<UmsMember> getAllUser(){

        List<UmsMember> umsMembers = userService.getAllUser();

        return umsMembers;
    }

    @ResponseBody
    @RequestMapping("index")
    public String index(){

        return "hello 加油！";

    }
}
