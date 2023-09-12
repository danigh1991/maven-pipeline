package com.core.topup.init;

import com.core.common.model.enums.EConfigurationResultType;
import com.core.common.model.enums.EConfigurationType;
import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.init.StaticInitializer;
import com.core.topup.model.enums.EOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component("TopUpStaticInitializer")
public class TopUpStaticInitializer implements StaticInitializer {

    private CommonService commonService;

    @Autowired
    public TopUpStaticInitializer(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostConstruct
    @Override
    public void init() {

        this.commonService.registerConfigModel("active_operator", EConfigurationType.NULL_STRING, EConfigurationResultType.STRING,"mci,mtn,ray");
        //MCI Topup Account Info
        this.commonService.registerConfigModel(EOperator.MCI.getTargetAccountConfigName(), EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.LONG,null);
        this.commonService.registerConfigModel("mci_min_amount", EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.LONG,"21000");
        this.commonService.registerConfigModel("mci_active_topup_package", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"1002,1003,1005");
        this.commonService.registerConfigModel("mci_active_offer_amount", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"1,4,5,6");

        //MTN Topup Account Info
        this.commonService.registerConfigModel(EOperator.MTN.getTargetAccountConfigName(), EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.LONG,null);
        this.commonService.registerConfigModel("mtn_min_amount", EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.LONG,"5000");
        this.commonService.registerConfigModel("mtn_active_topup_package", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"normal,amaizing,postpay");
        this.commonService.registerConfigModel("mtn_active_offer_amount", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"1,2,3,4,5,6");

        //RAY Topup Account Info
        this.commonService.registerConfigModel(EOperator.RAY.getTargetAccountConfigName(), EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.LONG,null);
        this.commonService.registerConfigModel("ray_min_amount", EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.LONG,"5000");
        this.commonService.registerConfigModel("ray_active_topup_package", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"normal,amaizing");
        this.commonService.registerConfigModel("ray_active_offer_amount", EConfigurationType.NOT_NULL_STRING, EConfigurationResultType.STRING,"1,4,5,6");


        this.commonService.registerConfigModel("checking_unknown_topup_request_schrate_minute", EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.INTEGER,"3");
        this.commonService.registerConfigModel("topup_mark_to_manual_control_after_minute", EConfigurationType.POSITIVE_NUMBER, EConfigurationResultType.INTEGER,"1440");
    }

    @Bean
    public int getCheckingUnknownTopUpRequestSchRateMinute()
    {
        return ((Integer) Utils.getCommonConfigValue("checking_unknown_topup_request_schrate_minute"))*60*1000;
    }
}
