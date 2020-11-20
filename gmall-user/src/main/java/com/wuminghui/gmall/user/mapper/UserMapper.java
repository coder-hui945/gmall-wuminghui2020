package com.wuminghui.gmall.user.mapper;


import com.wuminghui.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/23 - 16:55
 */
public interface UserMapper extends Mapper<UmsMember> {
    List<UmsMember> selectAllUser();
}
