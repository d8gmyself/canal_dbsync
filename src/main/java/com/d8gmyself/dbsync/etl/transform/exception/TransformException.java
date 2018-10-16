package com.d8gmyself.dbsync.etl.transform.exception;

/**
 * Created by ZhangDuo on 2016-3-10 17:47.
 * <p>
 * TRANSFORM异常，由TRANSFORM流程中抛出
 *
 * @author ZhangDuo
 */
public class TransformException extends RuntimeException {

    private static final long serialVersionUID = -9180772036237674263L;

    public TransformException() {
        super();
    }

    public TransformException(String message) {
        super(message);
    }

    public TransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformException(Throwable cause) {
        super(cause);
    }

    protected TransformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
