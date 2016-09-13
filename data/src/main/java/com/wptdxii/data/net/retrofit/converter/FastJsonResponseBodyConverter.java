package com.wptdxii.data.net.retrofit.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;
/**
 * Created by wptdxii on 2016/8/18 0018.
 */
final class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private Type type;
    private FastJsonConfig fastJsonConfig;
    public FastJsonResponseBodyConverter(FastJsonConfig fastJsonConfig, Type type) {
        this.fastJsonConfig = fastJsonConfig;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        ParserConfig config = fastJsonConfig.getParserConfig();
        int featureValues = fastJsonConfig.getFeatureValues();
        Feature[] features = fastJsonConfig.getFeatures();

        BufferedSource bufferedSource = Okio.buffer(value.source());
        String tempStr = bufferedSource.readUtf8();
        bufferedSource.close();
        
        return JSON.parseObject(tempStr,type, config, featureValues,
                features != null ? features : FastJsonConfig.EMPTY_SERIALIZER_FEATURES);
        
//        try {
//            return JSON.parseObject(value.string(), type, config, featureValues,
//                    features != null ? features : FastJsonConfig.EMPTY_SERIALIZER_FEATURES);
//        } finally {
//            value.close();
//        }
    }
}
