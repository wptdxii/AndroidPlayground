package com.wptdxii.domain.exception;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public interface ErrorBundle {
    Exception getException();
    String getErrorMessage();
}
