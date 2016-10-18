package com.wptdxii.playground.executor;

import com.wptdxii.domain.executor.PostExecutionThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wptdxii on 2016/9/17 0017.
 */
@Singleton
public class UIThread implements PostExecutionThread{
    @Inject
    public UIThread() {
    }

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
