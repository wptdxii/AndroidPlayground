package com.cloudhome.network;

import android.util.Log;

import com.cloudhome.application.MyApplication;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.utils.IpConfig;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthAvatar {
    private NetResultListener receiveDataListener;
    private PostFormBuilder builder;
    private Map<String, String> key_value = new HashMap<String, String>();

    private int action;
    private String errmsg;
    private String url;

    public AuthAvatar(NetResultListener receiveDataListener) {
        this.receiveDataListener = receiveDataListener;
        url = IpConfig.getUri2("avatar");
    }

    public void execute(Object... params) {

        key_value.put("user_id", params[0].toString());
        key_value.put("token", params[1].toString());
        key_value.put("name", params[3].toString());
        File file = new File(params[2].toString());
        action = (Integer) params[4];
        Log.i("AuthAvatar----url", url);
        Log.i("AuthAvatar----user_id", params[0].toString());
        Log.i("AuthAvatar----token", params[1].toString());
        Log.i("AuthAvatar----name", params[3].toString());
        Log.i("AuthAvatar----file", file.getName() + "-----");

        OkHttpClient mOkHttpClient = new OkHttpClient();
        //		RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);

		/*用okhttputils向java上传图片*/
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("user_id", params[0].toString())
                .addFormDataPart("token", params[1].toString())
                .addFormDataPart("name", params[3].toString())
                .build();
                /*
				这样不行
				.addPart(Headers.of(
						"Content-Disposition","form-data; name=\"user_id\""),
						RequestBody.create(null, params[0].toString()))
				.addPart(Headers.of(
						"Content-Disposition","form-data; name=\"token\""),
						RequestBody.create(null, params[1].toString()))
				.addPart(Headers.of(
						"Content-Disposition","form-data; name=\"name\""),
						RequestBody.create(null, params[3].toString()))
				.addPart(Headers.of(
						"Content-Disposition", "form-data; name=\"file\""),
						RequestBody.create(MediaType.parse("image/png"), file))*/

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        //		Response response =mOkHttpClient.newCall(request).execute();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                receiveDataListener.ReceiveData(action, MyApplication.NET_ERROR, null);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonBody = response.body().string();
                Log.d("onSuccess", "onSuccess json = " + jsonBody);
                try {
                    if (jsonBody.equals("") || jsonBody.equals("null")) {
                        receiveDataListener.ReceiveData(action, MyApplication.DATA_EMPTY, null);
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonBody);
                        String errcode = jsonObject.getString("errcode");
                        String errmsg = jsonObject.getString("errmsg");
                        if (errcode.equals("0")) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            String avatar = dataObj.getString("avatar");
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_OK, avatar);
                        } else {
                            receiveDataListener.ReceiveData(action, MyApplication.DATA_ERROR, errmsg);
                        }
                    }
                } catch (Exception e) {
                    receiveDataListener.ReceiveData(action, MyApplication.JSON_ERROR, null);
                }
            }
        });
    }
}
