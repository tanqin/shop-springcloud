package com.shop.search.dao;

import com.shop.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo, Long> {
}
