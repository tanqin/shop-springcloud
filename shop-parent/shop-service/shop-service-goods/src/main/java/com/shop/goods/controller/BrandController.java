package com.shop.goods.controller;

import com.shop.goods.pojo.Brand;
import com.shop.goods.service.BrandService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据 ID 查询品牌信息
     */
    @GetMapping("/{id}")
    public Result<Brand> findById(@PathVariable(value = "id") Integer id) {
        Brand brand = brandService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询品牌信息成功！", brand);
    }
}
