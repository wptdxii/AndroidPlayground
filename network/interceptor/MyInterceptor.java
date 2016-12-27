package com.cloudhome.network.interceptor;

import android.util.Log;

import com.cloudhome.utils.HttpMd5;
import com.cloudhome.utils.ParametersSorting;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;


/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences MAY VARY.
 * <p>
 * Created by tsuharesu on 4/1/15.
 */
public class MyInterceptor implements Interceptor {


    public static String device_id="";
    public static String sessionToken="";
    @Override
    public Response intercept(Chain chain) throws IOException {


        Request original = chain.request();

        Log.d("55551",  original.method());

        String url = original.url().toString();

        Log.d("55552",url);
        //请求定制：添加请求头
        Request.Builder requestBuilder = original.newBuilder();


        if( original.method().equals("GET")  ){



            requestBuilder.url(url+injectParams(url));


        }else if(original.method().equals("POST")){

            if(url.contains("mod=uploadPolicy")||url.contains("mod=setAvatar"))
            {
                requestBuilder.url(url+"&deviceId="+device_id+"&sessionToken="+sessionToken);

            }else {
                final Buffer buffer = new Buffer();
                original.body().writeTo(buffer);


                requestBuilder.url(url + injectParams(url + "&" + buffer.readUtf8()));
            }
        }


        Request request = requestBuilder.build();

        Response originalResponse = chain.proceed(request);

        return originalResponse;
    }


    /**
     * 当GET请求时，修改url的函数位置
     */

    public static String injectParams(String url) throws UnsupportedEncodingException {


        if (url != null && url.contains("?")) {


            Log.d("77771",url);
            String decodedURL = URLDecoder.decode(url, "UTF-8");

            Log.d("77773",decodedURL);


            String url_left  = decodedURL.substring(0,decodedURL.indexOf("?")+1);
            String url_right = decodedURL.substring(decodedURL.indexOf("?")+1, decodedURL.length());

            Log.d("7777774",url_left);
            Log.d("7777775",url_right);

            int i = 0;
            String split = "&";
            StringTokenizer token = new StringTokenizer(url_right, split);

            String[] url_key_value = new String[token.countTokens()];

            while (token.hasMoreTokens()) {

                url_key_value[i] = token.nextToken().toString();
                i++;
            }

            Map<String, String> key_value_map =new HashMap<String, String>();

            for(int j=0;j<url_key_value.length;j++)
            {
                String [] map_item = null;
                map_item = url_key_value[j].split("=");
                if(map_item.length<2)
                {
                    key_value_map.put(map_item[0],"");
                }else {
                    key_value_map.put(map_item[0], map_item[1]);
                }
            }


            key_value_map.put("deviceId", device_id);

            if(sessionToken==null || sessionToken.equals("null")){


                sessionToken="";
            }

            key_value_map.put("sessionToken",sessionToken);


            String paramurl= ParametersSorting.createLinkString(ParametersSorting.paraFilter(key_value_map)) ;


            Log.d("77777778",paramurl);
            String sign = HttpMd5.getMD5(paramurl + "bkyapp@bj");



            String  urlEncode = "&deviceId="+device_id+"&sessionToken="+sessionToken+"&sign="+sign;

            //   String urlEncode = url_left+Uri.encode( paramurl+"&sign="+sign);

            Log.d("7777779", urlEncode);


            return urlEncode;


        } else{

            return "";
        }
    }





}
