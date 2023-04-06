package com.shop.goods.service.impl;

import com.shop.goods.dao.BrandMapper;
import com.shop.goods.pojo.Brand;
import com.shop.goods.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询所有
     */
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据 ID 查询品牌信息
     *
     * @param id
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

    /**
     * 根据 ID 删除品牌
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 品牌条件查询
     *
     * @param brand
     */
    @Override
    public List<Brand> findList(Brand brand) {
        // 自定义搜索对象
        Example example = new Example(Brand.class);
        // 条件构造器
        Example.Criteria criteria = example.createCriteria();

        if (brand != null) {
            if (StringUtils.isNotEmpty(brand.getName())) {
                criteria.andLike("name", "%" + brand.getName() + "%");
            }
            if (StringUtils.isNotEmpty(brand.getLetter())) {
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }

        List<Brand> brandList = brandMapper.selectByExample(example);
        return brandList;
    }
}
