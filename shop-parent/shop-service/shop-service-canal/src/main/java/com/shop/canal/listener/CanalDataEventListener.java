package com.shop.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shop.content.feign.ContentFeign;
import com.shop.content.pojo.Content;
import com.shop.entity.Result;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {
    @Autowired
    private ContentFeign contentFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 增加数据监听
     *
     * @param eventType 事件类型
     * @param rowData   行数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println("增加后 ===> 列名：" + column.getName() + "---数据：" + column.getValue());
        }
    }

    /**
     * 修改数据监听
     *
     * @param rowData 行数据
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData) {
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println("修改前 ===> 列名：" + column.getName() + "---数据：" + column.getValue());
        }
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println("修改后 ===> 列名：" + column.getName() + "---数据：" + column.getValue());
        }
    }

    /**
     * 删除数据监听
     *
     * @param rowData 行数据
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.RowData rowData) {
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println("删除前 ===> 列名：" + column.getName() + "---数据：" + column.getValue());
        }
    }

    /**
     * 获取 categoryId
     *
     * @param eventType 事件类型
     * @param rowData   变化的行数据
     * @return categoryId 分类 id
     */
    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";

        if (eventType == CanalEntry.EventType.DELETE) {
            // 如果事件类型为「删除」，则获取 beforeList
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if ("category_id".equalsIgnoreCase(column.getName())) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        } else {
            // 如果事件类型为「修改」「新增」，则获取 afterList
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if ("category_id".equalsIgnoreCase(column.getName())) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }

        return categoryId;
    }

    /**
     * 自定义数据监听
     *
     * @param eventType 事件类型
     * @param rowData   变化的行数据
     */
    @ListenPoint(
            // 指定监听的目标地址
            destination = "example",
            // 指定监听的数据库
            schema = "changgou_content",
            // 指定监听的表
            table = {"tb_content", "tb_content_category"},
            // 指定监听的类型
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE, CanalEntry.EventType.INSERT}
    )
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        // 调用 feign 获取列名为 category_id 的值
        String categoryId = getColumnValue(eventType, rowData);
        // 调用 feign 获取该分类下的所有广告集合
        Result<List<Content>> contentListResult = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> contentList = contentListResult.getData();
        // 使用 redisTemplate 存储广告集合到 redis 中
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(contentList));


        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("自定义操作前 ===> 列名：" + column.getName() + "---数据：" + column.getValue());
        }
        rowData.getAfterColumnsList().forEach(column -> System.out.println("自定义操作后 ===> 列名：" + column.getName() + "---数据：" + column.getValue()));
    }
}
