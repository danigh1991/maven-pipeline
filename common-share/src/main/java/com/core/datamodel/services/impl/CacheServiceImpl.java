package com.core.datamodel.services.impl;

import com.core.datamodel.model.cachemodel.RelatedReference;
import com.core.datamodel.services.CacheService;
import com.core.services.ModelUtilityService;
import com.core.exception.ResourceNotFoundException;
import com.core.util.BaseUtils;
import com.hazelcast.core.*;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.query.SqlPredicate;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("CacheServiceImpl")
public class CacheServiceImpl  implements CacheService {

    private List<String> validCacheNames= Arrays.asList("relatedReferences", "enums","usersById","usersByUserName","userDetailsByUserName","userRegister","2faCodes","imageIdCounter",
                                                        "activeAndLiveCity","cityEName","externalVerification","antiCrawler","smsBodyCode",
                                                        "configuration","currencies","languages","financeDestNumber","redirectUrls",
                                                        "messageResources","multiLingualData","domains","mobileAppVersion","externalApi","userApiCall","topUpCache","shpOtpRequest");

    private List<String> noClearCacheNames= Arrays.asList("relatedReferences","externalVerification","confirmCodes","2faCodes","shpOtpRequest");

    @Value("${app.defaultTimeToLive}")
    int defaultTimeToLive;

    @Autowired
    private HazelcastInstance hz ;

    private HashMap<String,IMap<Integer, Object>> imapRepository=new HashMap<>();


    public CacheServiceImpl() {

    }

    private Boolean isValidChacheName(String cacheName){
        for (String cName:validCacheNames )
            if (cName.equalsIgnoreCase(cacheName))
                return true;
        return false;

    }

    private IMap getCacheIdMap(String cacheName){
        if (!isValidChacheName(cacheName))
            throw new ResourceNotFoundException("Imap Not found For This cache Name","global.cache.notFound" , cacheName );
        IMap<Integer, Object> map=imapRepository.get(cacheName);
        if (map==null) {
            map = hz.getMap(cacheName);
            imapRepository.putIfAbsent(cacheName,map);
        }
        return map;
        //return hz.getMap(cacheName);
    }

    @Override
    public String addEntryListenerToCache( MapListener mapListener,String cacheName, boolean includeValue){
        IMap<Integer, Object> map=getCacheIdMap(cacheName);
        return  map.addEntryListener(mapListener,includeValue);
    }

    @Override
    public Object getCacheValue(String cacheName, Object key) {
        return  getCacheIdMap(cacheName).get(key);
    }

    @Override
    public Set<Map.Entry> getCacheValues(String cacheName, String sqlPredicateString) {
        //new SqlPredicate("__key like '%ARTICLE_1260%'")
       if (BaseUtils.isStringSafeEmpty(sqlPredicateString))
           return  null;
        return getCacheIdMap(cacheName).entrySet(new SqlPredicate(sqlPredicateString));
    }

    @Override
    public Object putCacheValue(String cacheName,Object key,Object value) {
        return putCacheValue(cacheName,key,value,defaultTimeToLive, TimeUnit.MINUTES);
    }

    @Override
    public Object putCacheValue(String cacheName, Object key, Object value, List<RelatedReference> relatedReferences) {
        pushReferences(relatedReferences,cacheName,key,90,TimeUnit.DAYS);
        return putCacheValue(cacheName,key,value,defaultTimeToLive, TimeUnit.MINUTES);
    }

    @Override
    public Object putCacheValue(String cacheName, Object key, Object value, Integer timeToLive, TimeUnit timeUnit) {
        return getCacheIdMap(cacheName).put(key,value,timeToLive, timeUnit);
    }

    @Override
    public Object putCacheValue(String cacheName, Object key, Object value, List<RelatedReference> relatedReferences, Integer timeToLive, TimeUnit timeUnit) {
        pushReferences(relatedReferences,cacheName,key,90,TimeUnit.DAYS);
        return getCacheIdMap(cacheName).put(key,value,timeToLive, timeUnit);
    }

    private void pushReferences(List<RelatedReference> relatedReferences,String cacheName, Object key, Integer timeToLive, TimeUnit timeUnit){
        for (RelatedReference reference:relatedReferences) {
            Map<String, Set<Object>> relatedCacheNames = (Map<String, Set<Object>>) getCacheValue("relatedReferences", reference.getCacheKey());
            if (relatedCacheNames == null)
                relatedCacheNames = new HashMap<String, Set<Object>>();
            Set<Object> keys=relatedCacheNames.get(cacheName);
            if (keys==null)
                keys=new HashSet<>();
            keys.add(key);
            relatedCacheNames.put(cacheName, keys);
            putCacheValue("relatedReferences",reference.getCacheKey(), relatedCacheNames, timeToLive, timeUnit);
        }
    }

    @Override
    public Boolean refreshRelatedReferences(RelatedReference relatedReference) {
        Map<String, Set<Object>> relatedCacheNames = (Map<String, Set<Object>>) getCacheValue("relatedReferences", relatedReference.getCacheKey());
        if (relatedCacheNames == null)
            return true;
        Map<String,Set<Object>> tmp= new HashMap<String, Set<Object>>(relatedCacheNames);
        for (Map.Entry<String,Set<Object>> entry: tmp.entrySet()) {
            Set<Object> keys=entry.getValue();
            Set<Object> tmpKeys=new HashSet<>(keys);
            tmpKeys.forEach(k->{
                deleteCacheValue(entry.getKey(),k);
                keys.remove(k);
            });
            if(keys.size()==0)
                relatedCacheNames.remove(entry.getKey());
        }
        if (relatedCacheNames.size()>0)
            putCacheValue("relatedReferences",relatedReference.getCacheKey(), relatedCacheNames, 1,TimeUnit.DAYS);
        else
            deleteCacheValue("relatedReferences",relatedReference.getCacheKey());
        return true;
    }

    @Override
    public Boolean deleteCacheValue(String cacheName,Object key) {
        if (getCacheValue(cacheName,key)==null)
            return false;
        getCacheIdMap(cacheName).delete(key);
        return true;
    }

    @Override
    public void deleteCacheValues(String cacheName, List<Object> keys) {
        for ( Object key:keys) {
            if (getCacheValue(cacheName,key)!=null)
                getCacheIdMap(cacheName).delete(key);
        }
    }

    @Override
    public Boolean clearCacheValue(String cacheName) {
        getCacheIdMap(cacheName).clear();
        return true;
    }

    @Override
    public Boolean clearAllCacheValue() {
        for (String cacheName:validCacheNames ) {
            if(noClearCacheNames.stream().filter(c-> c==cacheName).count()<=0)
               getCacheIdMap(cacheName).clear();
        }
        return true;
    }
}
