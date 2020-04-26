package com.stivechen.java.signinwithapple.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * google guava cache 缓存抽象类
 * K,V为缓存数据类型
 *
 * @author chenbingran
 */
@Slf4j
public abstract class BaseGuavaCache<K, V> {

    /**缓存自动刷新周期*/
    protected int refreshDuration = 24;
    /**缓存刷新周期时间格式*/
    protected TimeUnit refreshTimeunit = TimeUnit.MINUTES;
    /**缓存过期时间（可选择*/
    protected int expireDuration = -1;
    /**缓存刷新周期时间格式*/
    protected TimeUnit expireTimeunit = TimeUnit.HOURS;
    /**缓存最大容量*/
    protected int maxSize = 16;
    /**数据刷新线程池*/
    protected static ListeningExecutorService refreshPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));

    /**缓存对象*/
    private LoadingCache<K, V> cache = null;

    /**
     * 用于初始化缓存值
     */
    public abstract void loadValueWhenStarted();

    /**
     * 缓存失效后，重新加载逻辑
     *
     * @param key
     * @return
     * @throws Exception
     */
    protected abstract V getValueWhenExpired(K key) throws Exception;

    /**
     * 获取缓存
     *
     * @param key
     * @return
     * @throws Exception
     */
    public V getValue(K key) throws Exception {
        try {
            return getCache().get(key);
        } catch (Exception e) {
            log.error("BaseGuavaCache get value from cache has exception key:{[}] error:{}", key, e);
            throw e;
        }
    }

    /**
     * 获取缓存，无则返回预设置的默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public V getValueOrDefault(K key, V defaultValue) {
        try {
            return getCache().get(key);
        } catch (Exception e) {
            log.error("BaseGuavaCache get value from cache has exception key:{[}] and return defaultValue:[{}] error:{}",
                    key, defaultValue, e);
            return defaultValue;
        }
    }

    public long getSize() {
        try {
            return getCache().size();
        } catch (Exception e) {
            log.error("BaseGuavaCache get cache size has exception:", e);
            throw e;
        }
    }

    /**
     * 设置基本属性
     */
    public BaseGuavaCache<K, V> setRefreshDuration(int refreshDuration) {
        this.refreshDuration = refreshDuration;
        return this;
    }

    public BaseGuavaCache<K, V> setRefreshTimeUnit(TimeUnit refreshTimeunit) {
        this.refreshTimeunit = refreshTimeunit;
        return this;
    }

    public BaseGuavaCache<K, V> setExpireDuration(int expireDuration) {
        this.expireDuration = expireDuration;
        return this;
    }

    public BaseGuavaCache<K, V> setExpireTimeUnit(TimeUnit expireTimeunit) {
        this.expireTimeunit = expireTimeunit;
        return this;
    }

    public BaseGuavaCache<K, V> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public void clearAll() {
        this.getCache().invalidateAll();
    }

    /**
     * 获取cache单例
     *
     * @return
     */
    private LoadingCache<K, V> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    CacheBuilder<Object, Object> cacheBuilder = CacheBuilder
                            .newBuilder()
                            .maximumSize(maxSize);

                    if (refreshDuration > 0) {
                        cacheBuilder = cacheBuilder.refreshAfterWrite(refreshDuration, refreshTimeunit);
                    }
                    if (expireDuration > 0) {
                        cacheBuilder = cacheBuilder.expireAfterWrite(expireDuration, expireTimeunit);
                    }

                    cache = cacheBuilder.build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K key) throws Exception {
                            return getValueWhenExpired(key);
                        }

                        @Override
                        public ListenableFuture<V> reload(final K key,
                                                          V oldValue) throws Exception {
                            return refreshPool.submit(new Callable<V>() {
                                public V call() throws Exception {
                                    return getValueWhenExpired(key);
                                }
                            });
                        }
                    });
                }
            }
        }

        return cache;
    }

}
