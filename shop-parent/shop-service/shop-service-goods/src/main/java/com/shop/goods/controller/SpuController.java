package com.shop.goods.controller;

import com.github.pagehelper.PageInfo;
import com.shop.entity.Result;
import com.shop.entity.StatusCode;
import com.shop.goods.pojo.Goods;
import com.shop.goods.pojo.Spu;
import com.shop.goods.service.SpuService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/
@Api(value = "SpuController")
@RestController
@RequestMapping("/spu")
@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    /***
     * Spu分页条件搜索实现
     * @param spu
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "Spu条件分页查询", notes = "分页条件查询Spu方法详情", tags = {"SpuController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "Spu对象", value = "传入JSON数据", required = false) Spu spu, @PathVariable int page, @PathVariable int size) {
        //调用SpuService实现分页条件查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(spu, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * Spu分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "Spu分页查询", notes = "分页查询Spu方法详情", tags = {"SpuController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用SpuService实现分页查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param spu
     * @return
     */
    @ApiOperation(value = "Spu条件查询", notes = "条件查询Spu方法详情", tags = {"SpuController"})
    @PostMapping(value = "/search")
    public Result<List<Spu>> findList(@RequestBody(required = false) @ApiParam(name = "Spu对象", value = "传入JSON数据", required = false) Spu spu) {
        //调用SpuService实现条件查询Spu
        List<Spu> list = spuService.findList(spu);
        return new Result<List<Spu>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "Spu根据ID删除", notes = "根据ID删除Spu方法详情", tags = {"SpuController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Long id) {
        //调用SpuService实现根据主键删除
        spuService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改Spu数据
     * @param spu
     * @param id
     * @return
     */
    @ApiOperation(value = "Spu根据ID修改", notes = "根据ID修改Spu方法详情", tags = {"SpuController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody @ApiParam(name = "Spu对象", value = "传入JSON数据", required = false) Spu spu, @PathVariable Long id) {
        //设置主键值
        spu.setId(id);
        //调用SpuService实现修改Spu
        spuService.update(spu);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增Spu数据
     * @param spu
     * @return
     */
    @ApiOperation(value = "Spu添加", notes = "添加Spu方法详情", tags = {"SpuController"})
    @PostMapping
    public Result add(@RequestBody @ApiParam(name = "Spu对象", value = "传入JSON数据", required = true) Spu spu) {
        //调用SpuService实现添加Spu
        spuService.add(spu);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询Spu数据
     * @param id
     * @return
     */
    @ApiOperation(value = "Spu根据ID查询", notes = "根据ID查询Spu方法详情", tags = {"SpuController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable Long id) {
        //调用SpuService实现根据主键查询Spu
        Spu spu = spuService.findById(id);
        return new Result<Spu>(true, StatusCode.OK, "查询成功", spu);
    }

    /***
     * 查询Spu全部数据
     * @return
     */
    @ApiOperation(value = "查询所有Spu", notes = "查询所Spu有方法详情", tags = {"SpuController"})
    @GetMapping
    public Result<List<Spu>> findAll() {
        //调用SpuService实现查询所有Spu
        List<Spu> list = spuService.findAll();
        return new Result<List<Spu>>(true, StatusCode.OK, "查询成功", list);
    }

    /**
     * 新增/修改商品
     *
     * @param goods 商品信息
     * @return
     */
    @PostMapping("/save")
    public Result saveGoods(@RequestBody Goods goods) {
        spuService.saveGoods(goods);
        return new Result(true, StatusCode.OK, "保存商品成功！");
    }

    /**
     * 根据 skuId 查询 goods 信息
     *
     * @param spuId
     * @return
     */
    @GetMapping("/goods/{id}")
    public Result<Goods> findGoodsById(@PathVariable(value = "id") Long spuId) {
        Goods goods = spuService.findGoodsById(spuId);
        return new Result<>(true, StatusCode.OK, "查询商品信息成功！", goods);
    }

    /**
     * 根据 spuId 审核商品
     */
    @PutMapping("/audit/{id}")
    public Result audit(@PathVariable(value = "id") Long spuId) {
        spuService.audit(spuId);
        return new Result(true, StatusCode.OK, "审核成功！");
    }

    /**
     * 根据 spuId 下架商品
     *
     * @param spuId
     * @return
     */
    @PutMapping("/pull/{id}")
    public Result pull(@PathVariable(value = "id") Long spuId) {
        spuService.pull(spuId);
        return new Result(true, StatusCode.OK, "下架成功！");
    }

    /**
     * 根据 spuId 上架商品
     *
     * @param spuId
     * @return
     */
    @PutMapping("/put/{id}")
    public Result put(@PathVariable(value = "id") Long spuId) {
        spuService.put(spuId);
        return new Result(true, StatusCode.OK, "上架成功！");
    }

    /**
     * 商品批量上架
     *
     * @param spuIds
     * @return
     */
    @PutMapping("/putMany")
    public Result putMany(@RequestBody Long[] spuIds) {
        spuService.putMany(spuIds);
        return new Result(true, StatusCode.OK, "上架成功！");
    }
}
