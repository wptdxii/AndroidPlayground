package com.cloudhome.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.cloudhome.network.interceptor.MyInterceptor;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class PostInterceptJavascriptInterface {
    public static final String TAG = "PostInterceptJavascriptInterface";

    private static String mInterceptHeader = null;
    private InterceptingWebViewClient mWebViewClient = null;


    public PostInterceptJavascriptInterface(InterceptingWebViewClient webViewClient) {
        mWebViewClient = webViewClient;
    }

    public static String enableIntercept(Context context, byte[] data) throws IOException {
        if (mInterceptHeader == null) {
            mInterceptHeader = new String(IOUtils.InputStreamTOByte(context.getAssets().open("www/interceptheader.html")), "utf-8");
        }


        Log.d("4444", Arrays.toString(data));
        org.jsoup.nodes.Document doc = Jsoup.parse(new String(data, "utf-8"));
        doc.outputSettings().prettyPrint(true);
        // Prefix every script to capture submits
        // Make sure our interception is the first element in the
        // header
        org.jsoup.select.Elements el = doc.getElementsByTag("head");
        if (el.size() > 0) {
            el.get(0).prepend(mInterceptHeader);
        }

        String pageContents = doc.toString();


        Log.d("777777", pageContents);

        return pageContents;
    }

    /**
     * 当POST请求时，修改url的函数位置
     */
    public static String injectIsParams(String url) throws UnsupportedEncodingException {


        if (url.equals("null")) {
            return "";
        }
        url= url +"&"+IpConfig.getCommon()+ "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

        String url_right = URLDecoder.decode(url, "UTF-8");

        Log.d("777773post", url);


        int i = 0;
        String split = "&";
        StringTokenizer token = new StringTokenizer(url_right, split);

        String[] url_key_value = new String[token.countTokens()];

        while (token.hasMoreTokens()) {

            url_key_value[i] = token.nextToken();
            i++;
        }

        Map<String, String> key_value_map = new HashMap<String, String>();

        for (int j = 0; j < url_key_value.length; j++) {
            String[] map_item = null;
            map_item = url_key_value[j].split("=");
            if (map_item.length < 2) {
                key_value_map.put(map_item[0], "");
            } else {
                key_value_map.put(map_item[0], map_item[1]);
            }
        }



        String paramurl = ParametersSorting.createLinkString(ParametersSorting.paraFilter(key_value_map));


        String sign = HttpMd5.getMD5(paramurl + "bkyapp@bj");

        //    Log.d("777771get", paramurl+"&sign="+sign);


        String urlEncode = url +"&sign=" + sign;

        //  String urlEncode = Uri.encode(paramurl+"&sign="+sign);

        return urlEncode;

    }

    @JavascriptInterface
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void customAjax(final String method, final String body) throws UnsupportedEncodingException {

        //  Log.i("77777",url);
        // injectIsParams(body);

        mWebViewClient.nextMessageIsAjaxRequest(new AjaxRequestContents(method, injectIsParams(body + "")));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @JavascriptInterface
    public void customSubmit(String json, String method, String enctype) {
        mWebViewClient.nextMessageIsFormRequest(
                new FormRequestContents(method, json, enctype));
    }


    public class FormRequestContents {
        public String method = null;
        public String json = null;
        public String enctype = null;

        public FormRequestContents(String method, String json, String enctype) {
            this.method = method;
            this.json = json;
            this.enctype = enctype;
        }
    }

    public class AjaxRequestContents {
        public String method = null;
        public String body = null;

        public AjaxRequestContents(String method, String body) {
            this.method = method;
            this.body = body;
        }
    }


}
