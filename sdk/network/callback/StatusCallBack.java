package com.cloudhome.network.callback;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.net.URLDecoder;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by bkyj-005 on 2017/1/18.
 */

public abstract class StatusCallBack extends MCallBack<String> {
    private Context context;

    public StatusCallBack(Context context) {
        this.context = context;
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        /*
        * 200   OK - [GET]：服务器成功返回用户请求的数据
        * 201 CREATED - [POST/PUT/PATCH]：用户新建或修改数据成功。
          202 Accepted - [*]：表示一个请求已经进入后台排队（异步任务）
          204 NO CONTENT - [DELETE]：用户删除数据成功。
          400 INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作
          401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）。
          403 Forbidden - [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的。
          404 NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
        * */
        if(response.code()>=200&&response.code()<300){
            return response.body().string();
        }else {
            Headers mHeaders=response.headers();
            String xError=mHeaders.get("x-error");
            if(!TextUtils.isEmpty(xError)){
                xError= URLDecoder.decode(xError,"utf-8");
                Looper.prepare();
                Toast.makeText(context, xError, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            return null;
        }
    }
}
