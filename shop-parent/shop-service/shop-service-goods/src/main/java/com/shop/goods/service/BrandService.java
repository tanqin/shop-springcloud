package com.shop.goods.service;

import com.github.pagehelper.PageInfo;
import com.shop.goods.pojo.Brand;

import java.util.List;

public interface BrandService {
    /**
     * 查询所有品牌
     */
    List<Brand> findAll();

    /**
     * 根据 ID 查询品牌信息
     *
     * @param id
     */
    Brand findById(Integer id);

    /**
     * 增加品牌
     *
     * @param brand
     */
    void add(Brand brand);

    /**
     * 修改品牌信息
     *
     * @param brand
     */
    void update(Brand brand);

    /**
     * 根据 ID 删除品牌
     *
     * @param id
     */
    void delete(Integer id);

    /**
     * 品牌条件查询
     *
     * @param brand
     */
    List<Brand> findList(Brand brand);

    /**
     * 品牌分页查询
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Integer page, Integer size);

    /**
     * 品牌条件分页查询
     *
     * @param brand
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Brand brand, Integer page, Integer size);
}
