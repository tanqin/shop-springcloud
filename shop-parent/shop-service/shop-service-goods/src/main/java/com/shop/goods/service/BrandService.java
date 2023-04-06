package com.shop.goods.service;

import com.shop.goods.pojo.Brand;

import java.util.List;

public interface BrandService {
    /**
     * 查询所有品牌
     */
    List<Brand> findAll();

    /**
     * 根据 ID 查询品牌信息
     */
    Brand findById(Integer id);
}
