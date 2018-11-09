package com.d8gmyself.dbsync.etl;

import com.d8gmyself.dbsync.etl.extract.ExtractTask;
import com.d8gmyself.dbsync.etl.load.LoadTask;
import com.d8gmyself.dbsync.etl.select.SelectTask;
import com.d8gmyself.dbsync.etl.transform.TransformTask;

/**
 * Created by ZhangDuo on 2016-3-15 14:21.
 * <p>
 * 对应一套SETL流程
 *
 * @author ZhangDuo
 */
public class SETLInstanceWithSpring {

    private SelectTask selectTask;
    private ExtractTask extractTask;
    private TransformTask transformTask;
    private LoadTask loadTask;

    public SelectTask getSelectTask() {
        return selectTask;
    }

    public void setSelectTask(SelectTask selectTask) {
        this.selectTask = selectTask;
    }

    public ExtractTask getExtractTask() {
        return extractTask;
    }

    public void setExtractTask(ExtractTask extractTask) {
        this.extractTask = extractTask;
    }

    public TransformTask getTransformTask() {
        return transformTask;
    }

    public void setTransformTask(TransformTask transformTask) {
        this.transformTask = transformTask;
    }

    public LoadTask getLoadTask() {
        return loadTask;
    }

    public void setLoadTask(LoadTask loadTask) {
        this.loadTask = loadTask;
    }

    /**
     * 启动流程
     */
    public void start() {
        this.selectTask.startup();
        this.loadTask.startup();
    }

    /**
     * 停止流程
     */
    public void stop() {
        this.selectTask.shutdown();
        this.loadTask.shutdown();
    }

}
