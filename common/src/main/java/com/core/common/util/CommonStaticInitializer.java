package com.core.common.util;

import com.core.common.resource.DBMessageSource;
import com.core.common.security.TokenHelper;
import com.core.common.services.CommonService;
import com.core.common.services.UserService;
import com.core.context.MultiLingualDataContextHolder;
import com.core.datamodel.model.wrapper.DomainWrapper;
import com.core.model.metadata.MultiLingualData;
import com.core.util.BaseUtils;
import com.core.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Component("commonStaticInitializer")
public class CommonStaticInitializer {

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DBMessageSource dbMessageSource;


    @PostConstruct
    public void init() {
        BeanUtil.setContext(context);
        BaseUtils.setDbMessageSource(dbMessageSource);
        Utils.setCommonService(commonService);
        Utils.setUserService(userService);
        Utils.setTokenHelper(tokenHelper);
        commonService.setDefaultTimeZone();

        //Language defaultLang = commonService.getDefaultLanguageInfo();
        DomainWrapper domainWrapper= commonService.getDefaultActiveDomainWrapper();
        MultiLingualData multiLingualData = new MultiLingualData(domainWrapper.getLanguageId(),domainWrapper.getLanguageLtrDirection(), domainWrapper.getLanguageShortName(), domainWrapper.getLanguageId(),domainWrapper.getLanguageLtrDirection(), domainWrapper.getLanguageShortName(),domainWrapper.getSiteThemeId(),domainWrapper.getCountryId().toString(), domainWrapper.getCountryName());
        MultiLingualDataContextHolder.setMultiLingualData(multiLingualData);
        MultiLingualDataContextHolder.getMultiLingualData().setActiveLanguageIds(commonService.getActiveLanguages().stream().map(l -> l.getId()).collect(Collectors.toList()));

    }
}
