package com.d8gmyself.dbsync.etl.select.selector;

import com.d8gmyself.dbsync.etl.commons.model.EventData;

import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-10 10:06.
 * <p>
 * 重新封装后的binlog事件
 *
 * @author ZhangDuo
 */
public class Message {

    /**
     * 批次号
     */
    private Long id;
    /**
     * binglog事件
     */
    private List<EventData> datas;

    public Message(Long id, List<EventData> datas) {
        this.id = id;
        this.datas = datas;
    }

    public Long getId() {
        return id;
    }

    public List<EventData> getDatas() {
        return datas;
    }
}
