package com.shop.goods.controller;

import com.shop.goods.pojo.Brand;
import com.shop.goods.service.BrandService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
@CrossOrigin // 允许跨域
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 查询所有品牌
     */
    @GetMapping
    public Result<List<Brand>> findAll() {
        List<Brand> list = brandService.findAll();
        return new Result<>(true, StatusCode.OK, "查询品牌集合成功！", list);
    }
}
