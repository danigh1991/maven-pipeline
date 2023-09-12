package com.core.datamodel.repository.factory;

import com.core.exception.ResourceNotFoundException;
import com.core.model.enums.ERepository;
import com.core.datamodel.repository.*;
import org.springframework.data.repository.Repository;

public  class ShareRepositoryFactory {

    private static ActivityRepository activityRepository;
    private static UserLogRepository userLogRepository;
    private static BaseBankRepository baseBankRepository;


    public static void setActivityRepository(ActivityRepository activityRepository) {
        ShareRepositoryFactory.activityRepository = activityRepository;
    }

    public static void setUserLogRepository(UserLogRepository userLogRepository) {
        ShareRepositoryFactory.userLogRepository = userLogRepository;
    }

    public static void setBaseBankRepository(BaseBankRepository baseBankRepository) {
        ShareRepositoryFactory.baseBankRepository = baseBankRepository;
    }

    public static Repository getRepository(ERepository eRepository){
        if (eRepository==ERepository.PANEL_MENU)
            return activityRepository;
        else if (eRepository==ERepository.USER_LOG)
            return userLogRepository;
        else if (eRepository==ERepository.BANK)
            return baseBankRepository;
        else
            throw new ResourceNotFoundException("","global.repositoryNotFound");
    }

}
