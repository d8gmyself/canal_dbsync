package com.d8gmyself.dbsync.etl.load.exception;

/**
 * Created by ZhangDuo on 2016-3-11 12:36.
 *
 * LOAD异常，由load过程中发成异常抛出
 *
 * @author ZhangDuo
 */
public class LoadException extends RuntimeException {

    private static final long serialVersionUID = 674449586758137822L;

    public LoadException() {
        super();
    }

    public LoadException(String message) {
        super(message);
    }

    public LoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadException(Throwable cause) {
        super(cause);
    }

    protected LoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
