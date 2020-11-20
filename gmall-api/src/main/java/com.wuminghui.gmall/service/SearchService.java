package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.PmsSearchParam;
import com.wuminghui.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/11/2 - 14:18
 */
public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
