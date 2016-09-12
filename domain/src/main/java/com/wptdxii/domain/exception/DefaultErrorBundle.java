package com.wptdxii.domain.exception;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public class DefaultErrorBundle implements ErrorBundle {
    private static final String DEFAULT_ERROR_MSG = "Unknown Error";
    private final Exception exception;

    public DefaultErrorBundle(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public String getErrorMessage() {
        return (exception != null) ? this.exception.getMessage() : DEFAULT_ERROR_MSG;
    }
}
