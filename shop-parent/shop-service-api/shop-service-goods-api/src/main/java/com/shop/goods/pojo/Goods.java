package com.shop.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 商品信息组合对象。由 spu 和 List<sku> 组成
 */
public class Goods implements Serializable {
    // spu 信息
    private Spu spu;

    // sku 信息
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
