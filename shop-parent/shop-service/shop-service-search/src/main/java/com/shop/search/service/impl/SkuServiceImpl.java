package com.shop.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.shop.entity.Result;
import com.shop.goods.feign.SkuFeign;
import com.shop.goods.pojo.Sku;
import com.shop.search.dao.SkuEsMapper;
import com.shop.search.pojo.SkuInfo;
import com.shop.search.service.SkuService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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
    public Map<String, Object> search(Map<String, String> searchMap) {
        // 搜索条件封装
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBasicQuery(searchMap);

        // 集合搜索
        Map<String, Object> resultMap = searchList(nativeSearchQueryBuilder);

        if (searchMap == null || StringUtils.isEmpty(searchMap.get("categoryName"))) {
            // 分组查询分类集合
            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
            resultMap.put("categoryList", categoryList);
        }
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brandName"))) {
            // 分组查询品牌集合
            List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
            resultMap.put("brandList", brandList);
        }

        // 分组查询规格集合
        Map<String, Set<String>> specList = searchSpecList(nativeSearchQueryBuilder);

        // 将 specList 添加至 resultMap 中
        resultMap.put("specList", specList);

        return resultMap;
    }

    private Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 添加分组查询条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));
        // 分组查询
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 获取规格分组数据
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = new ArrayList<>();
        // 遍历规格分组数据
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            // 其中一个规格
            String specName = bucket.getKeyAsString();
            specList.add(specName);
        }

        Map<String, Set<String>> allSpec = getAllSpec(specList);
        return allSpec;
    }

    /**
     * 获取规格集合
     *
     * @param specList
     * @return
     */
    private static Map<String, Set<String>> getAllSpec(List<String> specList) {
        // 定义存储规格数据的 Map 集合
        Map<String, Set<String>> allSpec = new HashMap<>();
        for (String spec : specList) {
            // 将规格字符串转为 Map 对象
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            // specMap 键值对进行集合操作，并遍历
            for (Map.Entry<String, String> specEntry : specMap.entrySet()) {
                // 规格名称
                String key = specEntry.getKey();
                // 规格值
                String value = specEntry.getValue();
                // 查找规格名称对应的规格值 Set 集合数据
                Set<String> specSet = allSpec.get(key);
                if (specSet == null) {
                    // 如果不存在 Set 集合数据，则定义一个 Set 集合
                    specSet = new HashSet<>();
                }
                // 将规格值添加至 Set 集合中
                specSet.add(value);
                // 将规格名称和规格值 Set 集合添加至 allSpec 中
                allSpec.put(key, specSet);
            }
        }
        return allSpec;
    }

    /**
     * 基础搜索
     *
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        // 创建搜索条件构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // 构建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 获取搜索条件
        if (searchMap != null && searchMap.size() > 0) {
            // 获取搜索关键词
            String keywords = (String) searchMap.get("keywords");

            if (!StringUtils.isEmpty(keywords)) {
                // 设置查询条件
//                nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));
            }

            // 分类搜索
            if (!StringUtils.isEmpty(searchMap.get("categoryName"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", searchMap.get("categoryName")));
            }
            // 品牌搜索
            if (!StringUtils.isEmpty(searchMap.get("brandName"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", searchMap.get("brandName")));
            }
            // 规格搜索
            for (Map.Entry<String, String> searchEntry : searchMap.entrySet()) {
                String searchKey = searchEntry.getKey();
                if (searchKey.startsWith("spec")) {
                    boolQueryBuilder.must(QueryBuilders.termQuery("specMap." + searchKey.substring(5) + ".keyword", searchEntry.getValue()));
                }
            }
            // 价格区间过滤搜索
            String price = (String) searchMap.get("price");
            if (!StringUtils.isEmpty(price)) {
                // 将 “元” 和 "以上" 中文字符去除
                price = price.replace("元", "").replace("以上", "");
                String[] priceArr = price.split("-");
                if (priceArr.length > 0) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(priceArr[0])));
                    if (priceArr.length == 2) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(priceArr[1])));
                    }
                }
            }

            // 排序搜索
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
                // 指定排序的域和排序规则
                nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sortField).order(SortOrder.valueOf(sortRule)));
            }

            // 分页搜索
            Integer pageNum = pagePageNum(searchMap);
            Integer pageSize = 30;
            nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, pageSize));


            // 将 boolQueryBuilder 设置给 nativeSearchQueryBuilder
            nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        }
        return nativeSearchQueryBuilder;
    }

    /**
     * 获取当前页
     */
    private Integer pagePageNum(Map<String, String> searchMap) {
        if (searchMap != null) {
            try {
                String pageNum = searchMap.get("pageNum");
                return Integer.parseInt(pageNum);
            } catch (NumberFormatException e) {
            }
        }
        return 1;
    }

    /**
     * 集合搜索
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Object> searchList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 创建搜索条件对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();

        // 分页搜索
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class);


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
        resultMap.put("total", total);
        resultMap.put("totalPages", totalPages);
        return resultMap;
    }

    /**
     * 分组查询分类集合
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
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
        return categoryList;
    }

    /**
     * 分组查询品牌集合
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 添加聚合查询
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        // 查询品牌数据
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 获品牌数据
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuBrand");
        // 品牌集合数据
        List<String> brandList = new ArrayList<>();
        // 循环品牌集合数据
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String brandName = bucket.getKeyAsString();
            brandList.add(brandName);
        }
        return brandList;
    }
}
