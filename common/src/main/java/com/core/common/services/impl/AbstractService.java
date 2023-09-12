package com.core.common.services.impl;


import com.core.datamodel.model.enums.ERoleType;
import com.core.exception.InvalidPermissionException;
import com.core.model.wrapper.ResultListPageable;
import com.core.repository.domain.MyPageRequest;
import com.core.datamodel.services.CacheService;
import com.core.common.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

public abstract class AbstractService {

    @Autowired
    private UserService userService;
    @Autowired
    private CacheService cacheService;


    public boolean hasRoleType(ERoleType eRoleType) {
        return userService.hasRoleType(eRoleType);
    }
    public boolean hasRole(String role) {
        return userService.hasExistRole(role);
    }
    public boolean hasRole(Long userId,String role) {
        return userService.hasExistRole(userId,role);
    }


    public  boolean hasPermission(String permission) {
        return userService.hasExistPermission(permission);
    }

    public void requiredForRole(String role){
        if (!hasRole(role)) {
            if (role=="supperbuser")
               throw new InvalidPermissionException("Access Denied", "common.storeBranch.create_rule");
            else
               throw new InvalidPermissionException("Access Denied", "global.accessDeniedMessage2");
        }
    }

    public PageRequest gotoPage(Integer start, Integer count, Sort.Direction sort, String sortField)
    {
        if (start == null  || count != null)
            return gotoDefaultPage();
        int page=0;
        int pageSize=0;
        if(start<0)
            start=0;
        if(count<1)
            count=1;
        if (start<count)
            page=0;
        else
            page=(start/count);
        pageSize=count;
        if (pageSize==0)
            pageSize=10;
        return new MyPageRequest(start,pageSize, sort,sortField);
    }
    public PageRequest gotoPage(Integer start, Integer count)
    {
        if (start == null  || count == null || (start == 0  && count == 0))
            return gotoDefaultPage();
        int page=0;
        int pageSize=0;
        if(start<0)
            start=0;
        if(count<1)
            count=1;
        if (start<count)
            page=0;
        else
            page=(start/count);
        pageSize=count;
        if (pageSize==0)
            pageSize=10;
        return new MyPageRequest(start,pageSize);
    }
    public PageRequest gotoDefaultPage()
    {
        return new MyPageRequest(0,10);
    }


    public ResultListPageable generatePageableResult(List resultList, Integer pageCount){
        Boolean hasNext=false;
        if  (resultList.size() > pageCount) {
            hasNext = true;
            resultList.remove(resultList.size()-1);
        }
        return new ResultListPageable(resultList, hasNext);
    }

    public Object getFromCache(String cacheName, String cacheKey){
        return  cacheService.getCacheValue(cacheName, cacheKey);
    }




}
