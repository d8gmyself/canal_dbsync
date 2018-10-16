package com.d8gmyself.dbsync.etl.load.loader;


import com.d8gmyself.dbsync.etl.commons.model.EventData;

import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-10 12:41.
 *
 * @author ZhangDuo
 */
public interface Loader {
    /**
     * 生成SQL语句
     */
    void changeToSqls(List<EventData> eventDatas);

    /**
     * 写入目标库
     */
    boolean load(List<EventData> eventDatas);
}
