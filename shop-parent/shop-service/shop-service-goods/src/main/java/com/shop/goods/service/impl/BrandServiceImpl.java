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

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }
}
