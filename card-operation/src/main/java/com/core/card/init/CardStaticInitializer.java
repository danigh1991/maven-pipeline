package com.core.card.init;

import com.core.common.model.enums.EConfigurationResultType;
import com.core.common.model.enums.EConfigurationType;
import com.core.common.services.CommonService;
import com.core.init.StaticInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component("cardStaticInitializer")
public class CardStaticInitializer implements StaticInitializer {

    private CommonService commonService;

    @Autowired
    public CardStaticInitializer(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostConstruct
    @Override
    public void init() {

        this.commonService.registerConfigModel("shpAcceptorCode", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"123456789012345");
        this.commonService.registerConfigModel("shpTerminalNumber", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"12345678");
        this.commonService.registerConfigModel("shpOtpLiveTimeSecond", EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.INTEGER,"120");
        this.commonService.registerConfigModel("shpTsmBaseUrl", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"http://185.167.72.9:8091");

        this.commonService.registerConfigModel("shpOtpHashAlgorithm", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"SHA256withRSA");
        this.commonService.registerConfigModel("cardPrivateKey", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,null);
        this.commonService.registerConfigModel("cardShpPublicKey", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,null);



    }
}
