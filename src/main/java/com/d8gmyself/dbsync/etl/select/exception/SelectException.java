package com.d8gmyself.dbsync.etl.select.exception;

/**
 * Created by ZhangDuo on 2016-3-10 19:29.
 * <p>
 * SELECT异常，由select过程中发成异常抛出
 *
 * @author ZhangDuo
 */
public class SelectException extends RuntimeException {

    private static final long serialVersionUID = -1396728679485743822L;

    public SelectException() {
        super();
    }

    public SelectException(String message) {
        super(message);
    }

    public SelectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectException(Throwable cause) {
        super(cause);
    }

    protected SelectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
