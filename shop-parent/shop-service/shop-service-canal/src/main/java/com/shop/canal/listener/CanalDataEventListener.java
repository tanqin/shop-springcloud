package com.shop.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {
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
     * 自定义数据监听
     *
     * @param rowData 行数据
     */
    @ListenPoint(
            // 指定监听的目标地址
            destination = "example",
            // 指定监听的数据库
            schema = "changgou_content",
            // 指定监听的表
            table = {"tb_content"},
            // 指定监听的类型
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE}
    )
    public void onEventCustom(CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("自定义操作前 ===> 列名：" + column.getName() + "---数据：" + column.getValue());
        }
        rowData.getAfterColumnsList().forEach(column -> System.out.println("自定义操作后 ===> 列名：" + column.getName() + "---数据：" + column.getValue()));
    }
}
