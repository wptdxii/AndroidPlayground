package com.wptdxii.data.net.okhttp.authenticator;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by wptdxii on 2016/8/4 0004.
 */
public class TokenAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return null;
    }
}
