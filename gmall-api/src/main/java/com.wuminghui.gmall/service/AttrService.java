package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.PmsBaseAttrInfo;
import com.wuminghui.gmall.bean.PmsBaseAttrValue;
import com.wuminghui.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

/**
 * @autor huihui
 * @date 2020/10/24 - 22:36
 */
public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();

    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet);
}
