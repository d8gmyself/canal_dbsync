package com.d8gmyself.dbsync.etl.extract.extractor;

import com.d8gmyself.dbsync.etl.commons.model.EventData;

/**
 * Created by ZhangDuo on 2016-3-10 12:41.
 *
 * @author ZhangDuo
 */
public interface Extractor {

    /**
     * 是否满足条件
     * @param eventData 要判断的数据
     */
    boolean isInvalid(EventData eventData);
}
