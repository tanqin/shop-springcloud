package com.shop.search.controller;

import com.shop.entity.Result;
import com.shop.entity.StatusCode;
import com.shop.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result importData() {
        skuService.importSku();
        return new Result(true, StatusCode.OK, "导入数据到索引库成功！");
    }

}
