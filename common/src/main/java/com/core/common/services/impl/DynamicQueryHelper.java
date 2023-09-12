package com.core.common.services.impl;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.enums.ERoleType;
import com.core.model.repository.impl.DynamicQueryRepository;
import com.core.model.wrapper.ResultListPageable;
import com.core.util.search.SearchCriteria;
import com.core.util.search.SearchCriteriaParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("dynamicQueryHelper")
public class DynamicQueryHelper {

    protected SearchCriteriaParser searchCriteriaParser;
    private DynamicQueryRepository dynamicQueryRepository;
    private CommonService commonService;

    @Autowired
    public DynamicQueryHelper(SearchCriteriaParser searchCriteriaParser, DynamicQueryRepository dynamicQueryRepository, CommonService commonService) {
        this.searchCriteriaParser = searchCriteriaParser;
        this.dynamicQueryRepository = dynamicQueryRepository;
        this.commonService = commonService;
    }
    public <T> ResultListPageable<T> getDataGeneral(Map<String, Object> requestParams, HashMap<String, List<String>> mapParams, String queryString, String countQueryString, Class<T> cls) {
        return this.getDataGeneral( requestParams,  mapParams,  queryString, countQueryString, cls,true);
    }

    public <T> ResultListPageable<T> getDataGeneral(Map<String, Object> requestParams, HashMap<String, List<String>> mapParams, String queryString, String countQueryString, Class<T> cls,Boolean userLimit) {
        if(!Utils.hasRoleType(ERoleType.ADMIN) && userLimit)
            requestParams.put("userId", Utils.getCurrentUserId());

        List<SearchCriteria> whereClauseParams = searchCriteriaParser.parseParams(mapParams, requestParams);

        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        List<String> sortParams = (List) requestParams.get("sortParams");
        List<String> groupParams = (List) requestParams.get("groupParams");

        Integer start = Utils.getAsIntegerFromMap(requestParams, "start", false,0);
        Integer count = Utils.getAsIntegerFromMap(requestParams, "count", false,commonService.getDefaultQueryCount());
        Boolean isNativeQuery=Utils.getAsBooleanFromMap(requestParams, "nativeQuery", false,true);
        ResultListPageable<T> result;
        result = dynamicQueryRepository.runDynamicQueryWithPaging(queryString,resultCount? countQueryString : "", whereClauseParams, start, count, sortParams,isNativeQuery,Utils.getAsStringFromMap(requestParams, "resultSetMapping", false, ""),groupParams,cls);
        return result;
    }

    public List<String> getSortParams(HashMap<String,List<String>> mapParams,String sortOptions){
        return  this.searchCriteriaParser.parseSortParams(mapParams,sortOptions);
    }


}
