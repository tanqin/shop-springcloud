package com.shop.search.controller;

import com.shop.entity.Result;
import com.shop.entity.StatusCode;
import com.shop.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;

    /**
     * 导入 Sku 数据
     *
     * @return
     */
    @GetMapping("/import")
    public Result importData() {
        skuService.importSku();
        return new Result(true, StatusCode.OK, "导入数据到索引库成功！");
    }

    /**
     * 条件搜索
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String, Object> searchMap) {
        return skuService.search(searchMap);
    }


}
