package com.github.bilak.springclassloaderissue.bootstrapping;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class CustomForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    private final Object SYNC = new Object();

    private volatile ClassLoader contextClassLoader;

    public CustomForkJoinWorkerThreadFactory() {
    }


    @Override
    public final ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
        return new CustomForkJoinWorkerThread(pool, getContextClassLoader());
    }

    @Override
    public String toString() {
        return "CustomForkJoinWorkerThreadFactory{classLoader=" + getContextClassLoader() + '}';
    }

    private ClassLoader getContextClassLoader() {
        if (contextClassLoader == null) {
            synchronized (SYNC) {
                if (contextClassLoader == null) {
                    contextClassLoader = Thread.currentThread().getContextClassLoader();
                }
            }
        }
        return contextClassLoader;
    }

    private static final class CustomForkJoinWorkerThread extends ForkJoinWorkerThread {

        private final ClassLoader contextClassLoader;

        private CustomForkJoinWorkerThread(
                final ForkJoinPool pool,
                final ClassLoader contextClassLoader
        ) {
            super(pool);
            super.setContextClassLoader(contextClassLoader);
            this.contextClassLoader = contextClassLoader;
        }

        @Override
        public void setContextClassLoader(ClassLoader cl) {
            super.setContextClassLoader(contextClassLoader);
        }
    }
}
