package com.shop.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.shop.entity.Result;
import com.shop.goods.feign.SkuFeign;
import com.shop.goods.pojo.Sku;
import com.shop.search.dao.SkuEsMapper;
import com.shop.search.pojo.SkuInfo;
import com.shop.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Override
    public void importSku() {
        // 通过 feign 查询所有 sku 数据
        Result<List<Sku>> skuListResult = skuFeign.findAll();
        // 将 Sku 数据转为 SkuInfo 数据
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);
        // 将 SkuInfo 数据存入 ES 中
        skuEsMapper.saveAll(skuInfoList);

    }
}
