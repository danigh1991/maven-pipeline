package com.core.datamodel.util;

import com.core.datamodel.repository.*;
import com.core.datamodel.repository.factory.ShareRepositoryFactory;
import com.core.datamodel.services.CacheService;
import com.core.services.CalendarService;
import com.core.services.ModelUtilityService;
import com.core.services.MultiLingualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("shareStaticContextInitializer")
public class ShareStaticContextInitializer {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserLogRepository userLogRepository;
    @Autowired
    private BaseBankRepository baseBankRepository;
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ModelUtilityService modelUtilityService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private MultiLingualService multiLingualService;


    @PostConstruct
    public void init() {

        ShareRepositoryFactory.setActivityRepository(activityRepository);
        ShareRepositoryFactory.setUserLogRepository(userLogRepository);
        ShareRepositoryFactory.setBaseBankRepository(baseBankRepository);
        ShareUtils.setCacheService(cacheService);
        ShareUtils.setModelUtilityService(modelUtilityService);
        ShareUtils.setCalendarService(calendarService);
        ShareUtils.setMultiLingualService(multiLingualService);

    }
}
