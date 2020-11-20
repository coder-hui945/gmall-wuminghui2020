//package com.wuminghui.gmall.user.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.wuminghui.gmall.service.UserService;
//import com.wuminghui.gmall.bean.UmsMember;
//import com.wuminghui.gmall.bean.UmsMemberReceiveAddress;
//import com.wuminghui.gmall.user.mapper.UmsMemberReceiveAddressMapper;
//import com.wuminghui.gmall.user.mapper.UserMapper;
//import com.wuminghui.gmall.util.RedisUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.Jedis;
//
//import java.util.List;
//
///**
// * @autor huihui
// * @date 2020/10/23 - 16:50
// */
//@Service
//public class UserServiceImpl implements UserService {
//    @Autowired
//    UserMapper userMapper;
//
//    @Autowired
//    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
//
//    @Autowired
//    RedisUtil redisUtil;
//
//    @Override
//    public List<UmsMember> getAllUser() {
//
//        List<UmsMember> umsMemberList = userMapper.selectAll();//userMapper.selectAllUser();
//
//        return umsMemberList;
//    }
//
//    @Override
//    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
//        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
////        Example example = new Example(UmsMemberReceiveAddress.class);
////        example.createCriteria().andEqualTo("memberId",memberId);
////        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
//
//        return umsMemberReceiveAddresses;
//    }
//
//    @Override
//    public UmsMember login(UmsMember umsMember) {
//
//        Jedis jedis = null;
//        try {
//
//            jedis = redisUtil.getJedis();
//            if (jedis != null) {//缓存存在
//
//                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");
//                if (StringUtils.isNotBlank(umsMemberStr)) {
//                    //密码正确
//                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
//                    return umsMemberFromCache;
//                } else {
//                    //密码错误
//                    //缓存中没有
//                    //开启数据库查询
//                    UmsMember umsMemberFromDb = loginFromDb(umsMember);
//                    if (umsMemberFromDb != null) {
//                        jedis.setex("user:" + umsMember.getPassword() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
//                    }
//                    return umsMemberFromDb;
//                }
//            } else {//缓存宕机，连接redis失败，访问db
//                UmsMember umsMemberFromDb = loginFromDb(umsMember);
//                if (umsMemberFromDb != null) {
//                    jedis.setex("user:" + umsMember.getPassword() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
//                }
//                return umsMemberFromDb;
//            }
//
//        } finally {
//            jedis.close();
//        }
//    }
//
//    @Override
//    public void addUserToken(String token, String memberId) {
//
//    }
//
//    @Override
//    public void addOauthUser(UmsMember umsMember) {
//
//    }
//
//    @Override
//    public void checkOauthUser(UmsMember umsCheck) {
//
//    }
//
//    private UmsMember loginFromDb(UmsMember umsMember) {
//
//        List<UmsMember> umsMembers = userMapper.select(umsMember);
//
//        if (umsMembers != null)
//            return umsMembers.get(0);
//
//        return null;
//    }
//}
