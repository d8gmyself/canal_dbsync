package com.d8gmyself.dbsync.etl.extract.exception;

/**
 * EXTRACT异常，由extract过程中发成异常抛出
 *
 * @author ZhangDuo
 */
public class ExtractException extends RuntimeException {

    private static final long serialVersionUID = 2680820522662343759L;

    public ExtractException() {
        super();
    }

    public ExtractException(String message) {
        super(message);
    }

    public ExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractException(Throwable cause) {
        super(cause);
    }

    public ExtractException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
