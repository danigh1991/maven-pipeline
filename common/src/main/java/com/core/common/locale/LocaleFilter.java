package com.core.common.locale;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.context.MultiLingualDataContextHolder;
import com.core.datamodel.model.wrapper.DomainWrapper;
import com.core.exception.MyException;
import com.core.model.metadata.MultiLingualData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class LocaleFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Value("${app.language_detect_type}")
    private String languageDetectType;

    @Autowired
    private CommonService commonService;

    @Autowired
    private LocaleResolver localeResolver;



    RequestMatcher requestMatcher = new RequestMatcher() {

        // Enabled Token protection on the following urls:
        private AntPathRequestMatcher[] requestMatcher = {
                // new AntPathRequestMatcher("/**")
                new AntPathRequestMatcher("/api/auth/login"),
                new AntPathRequestMatcher("/api/auth/logout"),
                new AntPathRequestMatcher("/api/captcha/generate"),
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/**/assets/**"),
                new AntPathRequestMatcher("/panel/**/*.*"),
                new AntPathRequestMatcher("/**/panel/**/*.*"),
                new AntPathRequestMatcher("/nimdalenap/**/*.*"),
                new AntPathRequestMatcher("/**/nimdalenap/**/*.*"),
                new AntPathRequestMatcher("/panelshare/**/*.*"),
                new AntPathRequestMatcher("/**/panelshare/**/*.*"),
                new AntPathRequestMatcher("/resources/**"),
                new AntPathRequestMatcher("/**/resources/**"),
                new AntPathRequestMatcher("/img/**"),
                new AntPathRequestMatcher("/**/img/**"),
                new AntPathRequestMatcher("/favicon.ico"),
                new AntPathRequestMatcher("/**/favicon.ico"),
                new AntPathRequestMatcher("/font/**"),
                new AntPathRequestMatcher("/**/font/**")
        };

        @Override
        public boolean matches(HttpServletRequest request) {
            // If the request match one url the CSFR protection will be enabled
            for (AntPathRequestMatcher rm : requestMatcher) {
                if (rm.matches(request)) { return true; }
            }
            return false;
        } // method matches

    };

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        DomainWrapper defaultDomainWrapper =commonService.getDefaultActiveDomainWrapper();
        DomainWrapper currentDomainWrapper=defaultDomainWrapper;

        String domainUrl = request.getServerName();
        List<DomainWrapper> domainWrappers=commonService.getActiveDomainWrappersByUrl(domainUrl);
        if(domainWrappers.size()>0)
            currentDomainWrapper=domainWrappers.get(0);
        else {
            domainWrappers.add(defaultDomainWrapper);
        }


        MultiLingualData multiLingualData = new MultiLingualData(defaultDomainWrapper.getLanguageId(),defaultDomainWrapper.getLanguageLtrDirection(), defaultDomainWrapper.getLanguageShortName(), currentDomainWrapper.getLanguageId(),currentDomainWrapper.getLanguageLtrDirection(), currentDomainWrapper.getLanguageShortName(),currentDomainWrapper.getSiteThemeId(),currentDomainWrapper.getCountryId().toString(), currentDomainWrapper.getCountryName());
        MultiLingualDataContextHolder.setMultiLingualData(multiLingualData);


        if (!requestMatcher.matches(request)){
            LocaleContextHolder.setLocale(localeResolver.resolveLocale(request));

            MultiLingualDataContextHolder.getMultiLingualData().setActiveLanguageIds(commonService.getActiveLanguages().stream().map(l -> l.getId()).collect(Collectors.toList()));
            if (languageDetectType == ELocalDetectType.domain.name()) {
                String domainRequest = request.getServerName();
            } else {
                String url = request.getRequestURI().substring(request.getContextPath().length());

                final String tmpFinalUrl =url;
                List<DomainWrapper> filterDomains = domainWrappers.stream().filter(l -> tmpFinalUrl.toLowerCase().contains(l.getLangInnerRoute().toLowerCase()) || tmpFinalUrl.toLowerCase().contains(l.getAngularLangRoute().toLowerCase()) || tmpFinalUrl.equalsIgnoreCase(l.getLangRoute())).collect(Collectors.toList());
                if (filterDomains.size() == 1) {
                    DomainWrapper cDomainWrapper = filterDomains.get(0);

                    String redirectUrl ="";
                    if ((!url.contains(cDomainWrapper.getLangInnerRoute()) && url.toLowerCase().contains(cDomainWrapper.getLangInnerRoute().toLowerCase())) ||
                        (!url.equals(cDomainWrapper.getLangRoute()) && url.equalsIgnoreCase(cDomainWrapper.getLangRoute())))
                        redirectUrl = url.toLowerCase().replace(cDomainWrapper.getLangInnerRoute().toLowerCase(), cDomainWrapper.getLangInnerRoute()).replace(cDomainWrapper.getLangRoute().toLowerCase(), cDomainWrapper.getLangRoute()) /*+ queryString*/;

                    String finalUrl = url.replace(cDomainWrapper.getAngularLangRoute(),"/").replace(cDomainWrapper.getLangInnerRoute(), "/").replace(cDomainWrapper.getLangRoute(), "/") /*+ queryString*/;
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentLanguageId(cDomainWrapper.getLanguageId());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentLanguageLeftToRight(cDomainWrapper.getLanguageLtrDirection());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentLanguageShortName(cDomainWrapper.getLanguageShortName());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentThemeId(cDomainWrapper.getSiteThemeId());

                    if(finalUrl.equalsIgnoreCase("/")) {
                        Long pageId = commonService.getDefaultSiteThemeDetails().getDefaultPageId();
                        if (pageId != null && pageId > 0) {
                            //finalUrl = "/" + pageAddress;
                        }
                    }

                    localeResolver.setLocale(request, response, cDomainWrapper.getLocale());
                    LocaleContextHolder.setLocale(localeResolver.resolveLocale(request));
                    if(!Utils.isStringSafeEmpty(redirectUrl)) {
                        Utils.redirect301(request, response, redirectUrl);
                    }else if (commonService.getDefaultLanguage().equals(cDomainWrapper.getId()) && !url.toLowerCase().contains(cDomainWrapper.getAngularLangRoute().toLowerCase())) {
                        Utils.redirect301(request, response, finalUrl);
                    } else {
                        Utils.urlDispatcher(request, response, finalUrl);
                    }
                } else {

                    localeResolver.setLocale(request, response, defaultDomainWrapper.getLocale());
                    LocaleContextHolder.setLocale(defaultDomainWrapper.getLocale());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentLanguageId(defaultDomainWrapper.getLanguageId());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentLanguageLeftToRight(defaultDomainWrapper.getLanguageLtrDirection());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentLanguageShortName(defaultDomainWrapper.getLanguageShortName());
                    MultiLingualDataContextHolder.getMultiLingualData().setCurrentThemeId(defaultDomainWrapper.getSiteThemeId());

                    try {
                        chain.doFilter(request, response);
                    }catch (RequestRejectedException e){
                        System.out.println("URL was not normalized >>>>>>>>>>>" + request.getRequestURI() + "<<<<<<<<<<<<<<<<");
                        throw new MyException("Invalid Url","global.urlNormalized_invalid");
                    }

                }
            }
        }else{
            try {
                chain.doFilter(request, response);
            }catch (RequestRejectedException e){
                System.out.println("URL was not normalized >>>>>>>>>>>" + request.getRequestURI() + "<<<<<<<<<<<<<<<<");
                throw new MyException("Invalid Url","global.urlNormalized_invalid");
            }
        }
    }

}
