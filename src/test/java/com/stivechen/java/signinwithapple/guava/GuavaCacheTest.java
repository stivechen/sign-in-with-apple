package com.stivechen.java.signinwithapple.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * JVM缓存 guava cache测试
 *
 * @author chenbingran
 */
public class GuavaCacheTest {

    @Test
    public void test_loadingCache() throws ExecutionException {
        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        //缓存加载机制：当value为空时默认返回该值
                        return "default";
                    }
                });

        loadingCache.put("k1", "kv1");
        String v1 = loadingCache.get("k1");
        System.out.println(v1);

        // 以不安全的方式获取缓存，当缓存不存在时，会通过CacheLoader自动加载
        String v2 = loadingCache.getUnchecked("k2");
        System.out.println(v2);

        // 获取缓存，当缓存不存在时，会通过CacheLoader自动加载
        String v3 = loadingCache.get("k3");
        System.out.println(v3);

        // 获取缓存，当缓存不存在时，通过当前指定的默认返回值加载
        String k4 = loadingCache.get("k4", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "kv4";
            }
        });
        System.out.println(k4);
    }

    @Test
    public void test_expireCache() throws Exception {
        LoadingCache <Integer, String> cache = CacheBuilder.newBuilder()
                //设置缓存在写入5秒钟后失效
                .expireAfterWrite(5, TimeUnit.SECONDS)
                //设置缓存的最大容量(基于容量的清除)
                .maximumSize(1000)
                .refreshAfterWrite(5, TimeUnit.SECONDS)
                //开启缓存统计
                .recordStats()
                .build(new CacheLoader<Integer, String>() {
                    @Override
                    public String load(Integer key) throws Exception {
                        System.out.println("从缓存加载中获取值---");
                        return "new value";
                    }
                });

        //单起一个线程监视缓存状态
        new Thread() {
            public void run() {
                while (true) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    System.out.println(sdf.format(new Date()) + " cache size: " + cache.size());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        //使用getIfPresent并不会触发load值
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        //写入缓存
        cache.put(1, "value1");
        //读取缓存
        System.out.println("write key:1 ,value:" + cache.getIfPresent(1));
        Thread.sleep(10000);
        // when write ,key:1 clear
        cache.put(2, "value2");
        System.out.println("write key:2 ,value:" + cache.getIfPresent(2));
        Thread.sleep(10000);
        // when read other key ,key:2 do not clear
        System.out.println(sdf.format(new Date()) + " after write, key:1 ,value:" + cache.getIfPresent(1));
        Thread.sleep(2000);
        // when read same key ,key:2 clear
        System.out.println(sdf.format(new Date()) + " final, key:2 ,value:" + cache.getIfPresent(2));
        Thread.sleep(2000);
        //cache.put(1, "value1");
        //cache.put(2, "value2");
        Thread.sleep(3000);
        System.out.println(sdf.format(new Date()) + " final key:1 ,value:" + cache.get(1));
        System.out.println(sdf.format(new Date()) + " final key:2 ,value:" + cache.get(2));
        //不需要重新load
        System.out.println(sdf.format(new Date()) + " final key:1 ,value:" + cache.get(1));
        System.out.println(sdf.format(new Date()) + " final key:2 ,value:" + cache.get(2));
        Thread.sleep(5000);
        System.out.println(sdf.format(new Date()) + " final key:1 ,value:" + cache.get(1));
        System.out.println(sdf.format(new Date()) + " final key:2 ,value:" + cache.get(2));
    }

}
