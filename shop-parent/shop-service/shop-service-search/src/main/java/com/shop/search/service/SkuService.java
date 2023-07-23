package com.shop.search.service;


import java.util.Map;

public interface SkuService {
    /**
     * 导入 Sku 数据
     */
    void importSku();

    /**
     * 条件搜索
     *
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String, Object> searchMap);
}
