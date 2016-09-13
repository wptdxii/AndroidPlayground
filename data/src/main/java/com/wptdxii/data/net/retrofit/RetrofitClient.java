package com.wptdxii.data.net.retrofit;

import com.wptdxii.data.BuildConfig;
import com.wptdxii.data.net.okhttp.OkClient;
import com.wptdxii.ext.Ext;

import java.io.File;
import java.lang.reflect.Field;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wptdxii on 2016/8/1 0001.
 * 单例模式
 */
public class RetrofitClient {
    private static final long TIMEOUT_READ = 15L;
    private static final long TIMEOUT_CONNECTION = 15L;

    /**
     * 全局统一的baseUrl
     */
    public static  String BASE_URL = "http://gank.io/";
    
    
    private static final String API_PRODUCT_URL = BASE_URL;
    private static final String API_DEV_URl = BASE_URL;

    private static final String WEB_DEV_URL = "http://your.api.com";
    private static final String WEB_PRODUCT_URL = "http://your.api.com";


    private static final boolean IS_DEV = BuildConfig.DEBUG;
    private static RetrofitClient mInstance;
    private static RetrofitClient mSyncInstance;

    /**
     * 当项目使用唯一的baseUrl时全局只创建一个Retrofit实例
     * 即该实例
     */
    private Retrofit mRetrofit;

    public static String getActiveHttpScheme() {
        return IS_DEV ? WEB_DEV_URL : WEB_PRODUCT_URL;
    }
    private static class RetrofitClientHolder {
        private static RetrofitClient mInstance = new RetrofitClient();
    }
    
    private static class SyncRetrofitClientHolder {
        private static final RetrofitClient mSyncInstance = new RetrofitClient(false);
    }

    /**
     * 创建Api接口的对象
     * 适用于项目中具有统一的baseUrl
     * 只创建一个Retrofit对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createApi(Class<T> clazz) {
        
        return mRetrofit.create(clazz);
    }

    /**
     * 创建使用RxJava的动态设置baseUrl的接口对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createMultiApi(Class<T> clazz) {
        return createApi(clazz, true);
    }

    /**
     * 创建不使用RxJava的动态设置baseUrl的接口对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createSyncMultiApi(Class<T> clazz) {
        return createApi(clazz, false);
    }

    /**
     * 利用反射，用于创建不同的baseUrl的Retrofit
     * 每调用一次就会创建一个Retrofit对象
     * @param clazz
     * @param userRxJava
     * @param <T>
     * @return
     */
    private  <T> T createApi(Class<T> clazz, boolean userRxJava) {
        String baseUrl = "";

        try {
            Field field = clazz.getField("BASE_URL");
            baseUrl = (String) field.get(clazz);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkClient.getInstance().getClient());

        if (userRxJava) {
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        
        Retrofit retrofit = builder.build();
        return retrofit.create(clazz);
    }
    
    public static RetrofitClient getInstance() {
        return RetrofitClientHolder.mInstance;
    }
    
    public static RetrofitClient getSynchronousInstance() {
        return SyncRetrofitClientHolder.mSyncInstance;
    }
    
    //私有化构造器
    private RetrofitClient() {
        this(true);
    }
    
    private RetrofitClient(boolean useRxJava) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//Bean中使用了 @SerializedName，需要使用该Converter
                .baseUrl(BuildConfig.DEBUG ? API_DEV_URl : API_PRODUCT_URL)
                .client(OkClient.getInstance().getClient());
//              .client(getClient());

        if (useRxJava) {
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        mRetrofit = builder.build();
    }

    
    private OkHttpClient getClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG?
                HttpLoggingInterceptor.Level.BODY: HttpLoggingInterceptor.Level.NONE);

        //设置缓存目录
        File httpCacheDirectory = new File(Ext.getApplication().getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        
        SSLSocketFactory sslSocketFactory = null;
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new OkHttpClient.Builder()
                //.addInterceptor(new HeaderInterceptor())
                .addInterceptor(loggingInterceptor)
               // .addInterceptor(new OfflineCacheInterceptor())//设置缓存策略
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                //.cache(cache)//设置缓存目录
               //.sslSocketFactory(sslSocketFactory)
                .build();
    }
    
    
}
