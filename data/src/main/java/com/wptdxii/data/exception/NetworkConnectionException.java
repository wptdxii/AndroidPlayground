package com.wptdxii.data.exception;

/**
 * Created by wptdxii on 2016/9/13 0013.
 */
public class NetworkConnectionException extends Exception {

    public NetworkConnectionException() {
        super();
    }

    public NetworkConnectionException(String message) {
        super(message);
    }

    public NetworkConnectionException(Throwable throwable) {
        super(throwable);
    }
    public NetworkConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
