package com.wuminghui.gmall.search.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.wuminghui.gmall.annotations.LoginRequired;
import com.wuminghui.gmall.bean.*;
import com.wuminghui.gmall.service.AttrService;
import com.wuminghui.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @autor huihui
 * @date 2020/11/1 - 16:28
 */
@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){//三级分类id，关键字，平台属性

        //调用搜索服务，返回搜索结果。
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList",pmsSearchSkuInfos);
        //抽取检索结果所包含的平台属性集合
        Set<String> valueIdSet = new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }

        //根据valueId将属性列表查询出来
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueId(valueIdSet);
        modelMap.put("attrList",pmsBaseAttrInfos);

        /*去掉当前valueId所在的属性组*/
        String[] delValueIds = pmsSearchParam.getValueId();

        if (delValueIds!=null){
            /*iterator()迭代器,删除数据后数组不会重新组合，导致数组下标越界出错*/
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            while (iterator.hasNext()){
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue attrValue : attrValueList) {
                    String valueId = attrValue.getId();
                    for (String delValueId : delValueIds) {
                        /*判断pmsBaseAttrInfo中属性是否有与pmsSearchParam中相同的属性*/
                        if (delValueId.equals(valueId)){
                            /*有该属性就删除该属性组*/
                            iterator.remove();
                        }
                    }
                }
            }
        }


        String urlParam = getUrlParam(pmsSearchParam);

        modelMap.put("urlParam",urlParam);
        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){

            modelMap.put("keyword",keyword);
        }
        //面包屑
        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
        if(delValueIds != null){
            //如果valueIds参数不等于空，表示当前请求中包含属性的参数，每一个属性参数会生成一个面包屑
            for (String delValueId : delValueIds) {
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                //生成面包屑的参数
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setValueName(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam,delValueId));
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
        }

        modelMap.put("attrValueSelectedList",pmsSearchCrumbs);


        return "list";
    }

    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam,String delValueId) {

        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] pmsSkuAttrValueList = pmsSearchParam.getValueId();



        String urlParam = "";
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;

        }
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if(pmsSkuAttrValueList != null){


            for (String pmsSkuAttrValue : pmsSkuAttrValueList) {
                if (!pmsSkuAttrValue.equals(delValueId)){

                    urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
                }
            }

        }

        return urlParam;
    }



    private String getUrlParam(PmsSearchParam pmsSearchParam) {

        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] pmsSkuAttrValueList = pmsSearchParam.getValueId();



        String urlParam = "";
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;

        }
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if(pmsSkuAttrValueList != null){

            for (String pmsSkuAttrValue : pmsSkuAttrValueList) {
                urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
            }

        }

        return urlParam;
    }

    @RequestMapping("index")
    @LoginRequired(loginSuccess = false)
    public String index(){
        return "index";
    }
}
