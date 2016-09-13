package com.wptdxii.data.net.retrofit.rx.func;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by wptdxii on 2016/8/24 0024.
 * 为RxJava提供网络失败重连机制
 * 默认情况下，最多重试3次，第一次会等3s，第二次会等6s，第三次会等9s
 */
public class RetryFunc implements Func1<Observable<? extends Throwable>,Observable<?>> {
    private int count = 3;//retry count
    private long delay = 3000L;//retry time

    public RetryFunc() {
    }

    public RetryFunc(int count) {
        this.count = count;
    }

    public RetryFunc(int count, long delay) {
        this.count = count;
        this.delay = delay;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable
                    .zipWith(Observable.range(1, count + 1), new Func2<Throwable, Integer, Wrapper>() {
                        @Override
                        public Wrapper call(Throwable throwable, Integer integer) {
                            return new Wrapper(integer,throwable);
                        }
                    })
                    .flatMap(new Func1<Wrapper, Observable<?>>() {
                        @Override
                        public Observable<?> call(Wrapper wrapper) {
                            Throwable throwable = wrapper.throwable;
                            if (throwable instanceof ConnectException
                                    || throwable instanceof SocketTimeoutException
                                    || throwable instanceof TimeoutException
                                    && wrapper.index < count + 1
                                    ) {
                                return Observable.timer(delay + (wrapper.index - 1) * delay, TimeUnit.MILLISECONDS);
                            }
                            return Observable.error(wrapper.throwable);
                        }
                    });
    }
    
    private class Wrapper {
        private int index;
        private Throwable throwable;

        public Wrapper(int index, Throwable throwable) {
            this.index = index;
            this.throwable = throwable;
        }
    }
}
