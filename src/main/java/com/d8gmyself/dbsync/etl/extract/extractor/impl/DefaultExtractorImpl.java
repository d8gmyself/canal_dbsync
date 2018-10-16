package com.d8gmyself.dbsync.etl.extract.extractor.impl;

import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.extract.extractor.Extractor;

/**
 * Created by ZhangDuo on 2016-3-10 12:41.
 *
 * @author ZhangDuo
 */
public class DefaultExtractorImpl implements Extractor {

    @Override
    public boolean isInvalid(EventData eventData) {
        return true;
    }

}
