Google Guava 本地缓存示例

    package com.github.hzredwall.common;
    
    import com.google.common.cache.CacheBuilder;
    import com.google.common.cache.CacheLoader;
    import com.google.common.cache.LoadingCache;
    import com.google.common.cache.Weigher;
    
    import java.util.concurrent.TimeUnit;
    
    
    /**
    * guava 缓存
    * @author RedWall
    * @mail walkmanlucas@gmail.com
    * @param 
    * @date 2018/11/15
    * @return 
    **/
    public abstract class BaseCache<K, V> {
        private LoadingCache<K,V> cache;
    
        public BaseCache()
        {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws Exception
                        {
                            return loadData(k);
                        }
                    });
        }
    
        /**
         * 超时缓存：数据写入缓存超过一定时间自动刷新
         * @param duration
         * @param timeUtil
         */
        public BaseCache(long duration, TimeUnit timeUtil)
        {
            cache = CacheBuilder.newBuilder()
                    .expireAfterWrite(duration, timeUtil)
                    .build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws Exception
                        {
                            return loadData(k);
                        }
                    });
        }
    
        /**
         * 限容缓存：缓存数据个数不能超过maxSize
         * @param maxSize
         */
        public BaseCache(long maxSize)
        {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(maxSize)
                    .build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws Exception
                        {
                            return loadData(k);
                        }
                    });
        }
    
        /**
         * 权重缓存：缓存数据权重和不能超过maxWeight
         * @param maxWeight
         * @param weigher：权重函数类，需要实现计算元素权重的函数
         */
        public BaseCache(long maxWeight, Weigher<K, V> weigher)
        {
            cache = CacheBuilder.newBuilder()
                    .maximumWeight(maxWeight)
                    .weigher(weigher)
                    .build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K k) throws Exception
                        {
                            return loadData(k);
                        }
                    });
        }
    
    
        /**
         *
         * 缓存数据加载方法
         * @author wangmeng
         * @param k
         * @return
         */
        protected abstract V loadData(K k);
    
        /**
         *
         * 从缓存获取数据
         * @author coshaho
         * @param param
         * @return
         */
        public V getCache(K param)
        {
            return cache.getUnchecked(param);
        }
    
        /**
         *
         * 清除缓存数据，缓存清除后，数据会重新调用load方法获取
         * @author wangmeng
         * @param k
         */
        public void refresh(K k)
        {
            cache.refresh(k);
        }
    
        /**
         *
         * 主动设置缓存数据
         * @author coshaho
         * @param k
         * @param v
         */
        public void put(K k, V v)
        {
            cache.put(k, v);
        }
    }


