package com.wuminghui.gmall.manage.mapper;

import com.wuminghui.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/24 - 22:39
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo>{
    List<PmsBaseAttrInfo> selectAttrValueListByValueId(@Param("valueIdStr") String valueIdStr);
}
