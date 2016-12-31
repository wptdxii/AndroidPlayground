package com.cloudhome.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.cloudhome.R;
import com.cloudhome.network.interceptor.MyInterceptor;
import com.squareup.mimecraft.FormEncoding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class InterceptingWebViewClient extends WebViewClient {
    public static final String TAG = "InterceptingWebViewClient";

    private Context mContext = null;
    private WebView mWebView = null;
    private PostInterceptJavascriptInterface mJSSubmitIntercept = null;

    private Dialog dialog ;
    private Boolean DialogShow;

    public InterceptingWebViewClient(Context context, WebView webView,Boolean DialogShow) {
        mContext = context;
        mWebView = webView;
        mJSSubmitIntercept = new PostInterceptJavascriptInterface(this);
        mWebView.addJavascriptInterface(mJSSubmitIntercept, "interception");
        this.DialogShow=DialogShow;
        dialog = new Dialog(context,R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("加载中...");
    }



    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO Auto-generated method stub
        super.onPageStarted(view, url, favicon);
        Log.i("click_____url---------", url);
        if(DialogShow) {
            dialog.show();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
        if(DialogShow) {
            dialog.dismiss();
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        // TODO Auto-generated method stub
        super.onReceivedError(view, errorCode, description, failingUrl);
        if(DialogShow) {
            dialog.dismiss();
        }
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        mNextAjaxRequestContents = null;
        mNextFormRequestContents = null;



        view.loadUrl(url);
        return true;
    }
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        if (request != null && request.getUrl() != null) {
//
//            String scheme = request.getUrl().getScheme().trim();
//            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
//
//
//
//
//                String urlstr =request.getUrl().toString();
//                try {
//                    // Our implementation just parses the response and visualizes it. It does not properly handle
//                    // redirects or HTTP errors at the moment. It only serves as a demo for intercepting POST requests
//                    // as a starting point for supporting multiple types of HTTP requests in a full fletched browser
//
//                    URL url = null;
//
//
//                    if (request.getMethod().equals("POST")) {
//                        url = new URL(urlstr);
//                        Log.d("77777post", urlstr);
//                    } else {
//                        Log.d("77777get", urlstr);
//                        url = new URL(injectIsParams(urlstr));
//
//                    }
//                    Log.d("77777get", url.toString());
//
//
//                    URLConnection rulConnection = url.openConnection();
//                    HttpURLConnection conn = (HttpURLConnection) rulConnection;
//                    conn.setRequestProperty("Accept-Charset", "utf-8");
//                    conn.setRequestProperty("contentType", "utf-8");
//                    conn.setRequestMethod(isPOST() ? "POST" : "GET");
//
//
//
//                    // Write body
//                    if (isPOST()) {
//                        OutputStream os = conn.getOutputStream();
//                        if (mNextAjaxRequestContents != null) {
//                            writeBody(os);
//                        } else {
//                            writeForm(os);
//                        }
//                        os.close();
//                    }
//
////           else{
////                String contentType = rulConnection.getContentType();
////                // If got a contentType header
////                if(contentType != null) {
////
////                    String mimeType = contentType;
////
////                    // Parse mime type from contenttype string
////                    if (contentType.contains(";")) {
////                        mimeType = contentType.split(";")[0].trim();
////                    }
////
////
////                    return new WebResourceResponse(mimeType, rulConnection.getContentEncoding(), rulConnection.getInputStream());
////                }
////
////            }
//
//
//
//
//
//                    // Read input
//                    String charset = conn.getContentEncoding() != null ? conn.getContentEncoding() : Charset.defaultCharset().displayName();
//                    String mime = conn.getContentType();
//                    byte[] pageContents = IOUtils.InputStreamTOByte(conn.getInputStream());
//
//                    Log.d("888888mime",mime);
//                    // Perform JS injection
//                    if (mime.equals("text/html")&&isPOST()) {
//                        pageContents = PostInterceptJavascriptInterface
//                                .enableIntercept(mContext, pageContents)
//                                .getBytes(charset);
//                    }
//
//
//
//                    if (mime.contains(";")) {
//                        mime = mime.split(";")[0].trim();
//                    }
//
//
//                    Log.d("888888", charset);
//                    // Convert the contents and return
//                    InputStream isContents = new ByteArrayInputStream(pageContents);
//
//                    mNextAjaxRequestContents=null;
//                    return new WebResourceResponse(mime, charset,
//                            isContents);
//
//
//
//                } catch (FileNotFoundException e) {
//                    Log.w("Error 404", "Error 404:" + e.getMessage());
//                    e.printStackTrace();
//
//                    return null;        // Let Android try handling things itself
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                    return null;        // Let Android try handling things itself
//                }
//            }
//        }
//        return null;
//    }
//


    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, final String urlstr) {
        try {
            // Our implementation just parses the response and visualizes it. It does not properly handle
            // redirects or HTTP errors at the moment. It only serves as a demo for intercepting POST requests
            // as a starting point for supporting multiple types of HTTP requests in a full fletched browser

            URL url = null;



            if (isPOST()) {
                url = new URL(urlstr);
                Log.d("77777post", urlstr);
            } else {
                Log.d("77777get", urlstr);
                url = new URL(injectIsParams(urlstr));

            }
            Log.d("77777get", url.toString());


            URLConnection rulConnection = url.openConnection();
            HttpURLConnection conn = (HttpURLConnection) rulConnection;
            conn.setRequestMethod(isPOST() ? "POST" : "GET");
            conn.connect();


            // Write body
            if (isPOST()) {
                OutputStream os = conn.getOutputStream();
                if (mNextAjaxRequestContents != null) {
                    writeBody(os);
                } else {
                    writeForm(os);
                }
                os.close();
            }

//           else{
//                String contentType = rulConnection.getContentType();
//                // If got a contentType header
//                if(contentType != null) {
//
//                    String mimeType = contentType;
//
//                    // Parse mime type from contenttype string
//                    if (contentType.contains(";")) {
//                        mimeType = contentType.split(";")[0].trim();
//                    }
//
//
//                    return new WebResourceResponse(mimeType, rulConnection.getContentEncoding(), rulConnection.getInputStream());
//                }
//
//            }





            // Read input
            String charset = conn.getContentEncoding() != null ? conn.getContentEncoding() : Charset.defaultCharset().displayName();
            String mime = conn.getContentType();
            byte[] pageContents = IOUtils.InputStreamTOByte(conn.getInputStream());

            Log.d("888888mime",mime);
            // Perform JS injection
            if (mime.equals("text/html")&&isPOST()) {
                pageContents = PostInterceptJavascriptInterface
                        .enableIntercept(mContext, pageContents)
                        .getBytes(charset);
            }


            if (mime.contains(";")) {

                mime = mime.split(";")[0].trim();

            }


            Log.d("888888", charset);


            // Convert the contents and return

            InputStream isContents = new ByteArrayInputStream(pageContents);

            mNextAjaxRequestContents=null;
            return new WebResourceResponse(mime, charset,
                    isContents);



        } catch (FileNotFoundException e) {
            Log.w("Error 404", "Error 404:" + e.getMessage());
            e.printStackTrace();

            return null;        // Let Android try handling things itself
        } catch (Exception e) {
            e.printStackTrace();

            return null;        // Let Android try handling things itself
        }
    }

    private boolean isPOST() {


        return (mNextAjaxRequestContents!=null&&mNextAjaxRequestContents.method.equals("POST"));

    }



    private void writeBody(OutputStream out) {
        try {
            Log.d("777773", mNextAjaxRequestContents.body);
            out.write(mNextAjaxRequestContents.body.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected void writeForm(OutputStream out) {
        try {
            JSONArray jsonPars = new JSONArray(mNextFormRequestContents.json);

            // We assume to be dealing with a very simple form here, so no file uploads or anything
            // are possible for reasons of clarity
            FormEncoding.Builder m = new FormEncoding.Builder();
            for (int i = 0; i < jsonPars.length(); i++) {
                JSONObject jsonPar = jsonPars.getJSONObject(i);

                m.add(jsonPar.getString("name"), jsonPar.getString("value"));
                // jsonPar.getString("type");
                // TODO TYPE?
            }
            m.build().writeBodyTo(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getType(Uri uri) {
        String contentResolverUri = mContext.getContentResolver().getType(uri);
        if (contentResolverUri == null) {
            contentResolverUri = "*/*";
        }
        return contentResolverUri;
    }

    private PostInterceptJavascriptInterface.FormRequestContents mNextFormRequestContents = null;

    public void nextMessageIsFormRequest(PostInterceptJavascriptInterface.FormRequestContents formRequestContents) {
        mNextFormRequestContents = formRequestContents;
    }

    private PostInterceptJavascriptInterface.AjaxRequestContents mNextAjaxRequestContents = null;

    public void nextMessageIsAjaxRequest(PostInterceptJavascriptInterface.AjaxRequestContents ajaxRequestContents) {

        mNextAjaxRequestContents= ajaxRequestContents;
    }

    /**
     * 当GET请求时，修改url的函数位置
     */

    public static String injectIsParams(String url) throws UnsupportedEncodingException {


        if (url != null && url.contains("?")) {


            url=url+"&"+IpConfig.getCommon()+ "deviceId=" + MyInterceptor.device_id + "&sessionToken=" + MyInterceptor.sessionToken;

            if(!url.contains("gateway"))
            {


                return url;
            }




            Log.d("77771", url);
            String decodedURL = URLDecoder.decode(url, "UTF-8");

            String url_left = decodedURL.substring(0, decodedURL.indexOf("?") + 1);
            String url_right = decodedURL.substring(decodedURL.indexOf("?") + 1, decodedURL.length());

            Log.d("7777774", url_left);
            Log.d("7777775", url_right);

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


            String urlEncode = url + "&sign=" + sign;

            //   String urlEncode = url_left+Uri.encode( paramurl+"&sign="+sign);

            Log.d("7777779", urlEncode);


            return urlEncode;

        }else{
            Log.d("777775", url);
            return url;
        }
    }


}
