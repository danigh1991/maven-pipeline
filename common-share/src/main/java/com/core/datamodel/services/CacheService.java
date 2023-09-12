package com.core.datamodel.services;



import com.core.datamodel.model.cachemodel.RelatedReference;
import com.hazelcast.map.listener.MapListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface CacheService {


    String addEntryListenerToCache(MapListener var1, String cacheName, boolean var2);
    Object getCacheValue(String cacheName,Object key);
    Set<Map.Entry> getCacheValues(String cacheName, String sqlPredicateString) ;
    Object putCacheValue(String cacheName,Object key,Object value);
    Object putCacheValue(String cacheName,Object key,Object value,List<RelatedReference> relatedReferences);
    Object putCacheValue(String cacheName,Object key,Object value,Integer timeToLive, TimeUnit timeUnit);
    Object putCacheValue(String cacheName, Object key, Object value, List<RelatedReference> relatedReferences, Integer timeToLive, TimeUnit timeUnit);
    Boolean refreshRelatedReferences(RelatedReference relatedReference);
    Boolean deleteCacheValue(String cacheName,Object key);
    void deleteCacheValues(String cacheName,List<Object> keys);
    Boolean clearCacheValue(String cacheName);
    Boolean clearAllCacheValue();
}
