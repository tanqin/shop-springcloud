package com.shop.goods.service.impl;

import com.shop.goods.dao.BrandMapper;
import com.shop.goods.pojo.Brand;
import com.shop.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据 ID 查询品牌信息
     *
     * @param id
     * @return
     */
    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加品牌
     *
     * @param brand
     */
    @Override
    public void add(Brand brand) {
        // insertSelective 只会选择有值的项
        brandMapper.insertSelective(brand);
    }

    /**
     * 修改品牌信息
     *
     * @param brand
     */
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }
}
