package com.wptdxii.domain.executor;

import rx.Scheduler;

/**
 * Created by wptdxii on 2016/9/12 0012.
 */
public interface PostExecutionThread {
    Scheduler getScheduler();
}
