package com.wptdxii.data.net.retrofit.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by wptdxii on 2016/8/17 0017.
 */
public final class FastJsonConverterFactory extends Converter.Factory {
    public static FastJsonConverterFactory create() {
        return create(new FastJsonConfig());
    }

    private static FastJsonConverterFactory create(FastJsonConfig fastJsonConfig) {
        return new FastJsonConverterFactory(fastJsonConfig);
    }
    
    private final FastJsonConfig fastJsonConfig;

    private FastJsonConverterFactory(FastJsonConfig fastJsonConfig) {
        if(fastJsonConfig == null) throw new NullPointerException("fastJsonConfig == null");
        this.fastJsonConfig = fastJsonConfig;
    }
    
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FastJsonResponseBodyConverter<>(fastJsonConfig,type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>(fastJsonConfig);
    }
}
