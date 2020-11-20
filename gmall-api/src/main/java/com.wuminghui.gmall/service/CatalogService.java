package com.wuminghui.gmall.service;

import com.wuminghui.gmall.bean.PmsBaseCatalog1;
import com.wuminghui.gmall.bean.PmsBaseCatalog2;
import com.wuminghui.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @autor huihui
 * @date 2020/10/24 - 12:47
 */
public interface CatalogService {

    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
