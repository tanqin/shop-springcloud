package com.shop.goods.feign;

import com.shop.entity.Result;
import com.shop.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /***
     * 查询Sku全部数据
     * @return
     */
    @GetMapping
    Result<List<Sku>> findAll();
}
