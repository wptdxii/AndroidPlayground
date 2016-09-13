package com.wptdxii.ext.util.json;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wptdxii on 2016/8/24 0024.
 */
public class JsonConvert<T> extends AbsConvert<T> {
    private String field = null;

    /**
     * 当返回的数据是JSONObject时，设置想要获取的字段
     * 泛型也要改为相对的
     *
     * @param field
     */
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public T parse(String result) {
        T t = null;

        try {
            Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Gson gson = new Gson();
            if (!TextUtils.isEmpty(field)) {
                JSONObject jsonObject = new JSONObject(result);
                t = gson.fromJson(jsonObject.getString(field), type);
            } else {
                t = gson.fromJson(result, type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return t;
    }
}
