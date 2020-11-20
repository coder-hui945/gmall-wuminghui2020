package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.UmsMember;
import com.wuminghui.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/23 - 16:49
 */
public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String id);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    public UmsMember addOauthUser(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsCheck);

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);
}
