package com.wptdxii.data.executor;

import com.wptdxii.domain.executor.ThreadExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Decorated ThreadPoolExecutor
 * Created by wptdxii on 2016/9/13 0013.
 */
@Singleton
public class JobExecutor implements ThreadExecutor {

    private static final int INITIAL_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;

    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> workQueue;
    private final ThreadFactory threadFactory;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public JobExecutor() {
        this.workQueue = new LinkedBlockingDeque<>();
        this.threadFactory = new JobThreadFactory();
        this.threadPoolExecutor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, this.workQueue, this.threadFactory);

    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new IllegalArgumentException("Runnable to execute cannot be null");
        }

        this.threadPoolExecutor.execute(command);

    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "android_";
        private int counter;

        @Override
        public Thread newThread(Runnable command) {
            return new Thread(command, THREAD_NAME + (counter++));
        }
    }
}
