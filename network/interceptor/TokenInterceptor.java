package com.cloudhome.network.interceptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.Util.UTF_8;


/**
 * Created by bkyj-005 on 2016/10/20.
 */

public class TokenInterceptor implements Interceptor {
    private Context mContext;
    public TokenInterceptor(Context context) {
        this.mContext = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        //注意 >>>>>>>>> okhttp3.4.1这里变成了 !HttpHeader.hasBody(response)
        if (!HttpEngine.hasBody(response)) {
//        if(!HttpHeader.hasBody(response)){
            //END HTTP
        } else if (bodyEncoded(response.headers())) {
            //HTTP (encoded body omitted)
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF_8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF_8);
                } catch (UnsupportedCharsetException e) {
                    //Couldn't decode the response body; charset is likely malformed.
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
//                L.i("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                return response;
            }

            if (contentLength != 0) {
                String result = buffer.clone().readString(charset);

                //获取到response的body的string字符串
                //do something .... <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                Log.i("intercept",result);
                if(result.contains("errcode")){
                    try {
                        JSONObject resultObj=new JSONObject(result);
                        int resultErrcode= Integer.parseInt(resultObj.get("errcode").toString());
                        if(resultErrcode>5000){
                            String errMsg=resultObj.getString("errmsg");
                            if(resultErrcode==5002||resultErrcode==5003){
                                Intent intent = new Intent("cloudhome.token.outofdate");
                                mContext.sendBroadcast(intent);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

//            L.i("<-- END HTTP (" + buffer.size() + "-byte body)");
        }
        return response;
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}
