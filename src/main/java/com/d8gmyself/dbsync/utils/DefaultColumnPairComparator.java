package com.d8gmyself.dbsync.utils;

import com.d8gmyself.dbsync.commons.model.ColumnPair;

import java.util.Comparator;

/**
 * Created by ZhangDuo on 2016-3-15 10:54.
 *
 * @author ZhangDuo
 */
public class DefaultColumnPairComparator implements Comparator<ColumnPair> {

    public static final Comparator<ColumnPair> INSTANCE = new DefaultColumnPairComparator();

    private DefaultColumnPairComparator() {
    }

    @Override
    public int compare(ColumnPair o1, ColumnPair o2) {
        int x = o1.getSrcName().hashCode() - o2.getSrcName().hashCode();
        return Integer.compare(x, 0);
    }
}
