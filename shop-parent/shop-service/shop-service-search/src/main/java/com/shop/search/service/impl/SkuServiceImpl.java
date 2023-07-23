package com.shop.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.shop.entity.Result;
import com.shop.goods.feign.SkuFeign;
import com.shop.goods.pojo.Sku;
import com.shop.search.dao.SkuEsMapper;
import com.shop.search.pojo.SkuInfo;
import com.shop.search.service.SkuService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 导入 Sku
     */
    @Override
    public void importSku() {
        // 通过 feign 查询所有 sku 数据
        Result<List<Sku>> skuListResult = skuFeign.findAll();
        // 将 Sku 数据转为 SkuInfo 数据
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);
        // 生成规格参数动态域
        for (SkuInfo skuInfo : skuInfoList) {
            // 将 spec 字符串转为 map 形式
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec());
            // 设置 specMap
            skuInfo.setSpecMap(specMap);
        }

        // 将 SkuInfo 数据存入 ES 中
        skuEsMapper.saveAll(skuInfoList);
    }


    /**
     * 条件搜索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        // 创建搜索条件构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // 获取搜索条件
        if (searchMap != null && searchMap.size() > 0) {
            // 获取搜索关键词
            String keywords = (String) searchMap.get("keywords");

            if (!StringUtils.isEmpty(keywords)) {
                // 设置查询条件
                nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }
        }

        // 创建搜索条件对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();

        // 分页搜索
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class);

        // 分组查询分类集合
        // 添加聚合查询
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        // 查询分类数据
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 获分类数据
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuCategory");
        // 分类集合数据
        List<String> categoryList = new ArrayList<>();
        // 循环分类集合数据
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }


        // 获取搜索结果
        // 1. 获取数据
        List<SkuInfo> skuInfoList = skuPage.getContent();
        // 2. 获取总记录数
        long total = skuPage.getTotalElements();
        // 3. 获取总页数
        int totalPages = skuPage.getTotalPages();

        // 设置返回结果 Map<String, Object>
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", skuInfoList);
        resultMap.put("categoryList", categoryList);
        resultMap.put("total", total);
        resultMap.put("totalPages", totalPages);

        return resultMap;
    }

}
