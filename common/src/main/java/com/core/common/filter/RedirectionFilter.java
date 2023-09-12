package com.core.common.filter;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.RedirectUrl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class RedirectionFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private CommonService commonService;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        List<RedirectUrl> redirectUrlList= commonService.getActiveRedirectUrls();
        String url = Utils.getAsciiEncodeUrl(request.getRequestURI().substring(request.getContextPath().length()));
        List<RedirectUrl> filterRedirectUrl=redirectUrlList.stream().filter(r -> url.equals(Utils.getAsciiEncodeUrl(r.getFrom()))).collect(Collectors.toList());
        if (filterRedirectUrl.size() == 1) {
             if (filterRedirectUrl.get(0).getMoveType()== HttpServletResponse.SC_MOVED_PERMANENTLY)
                Utils.redirect301(request,response,filterRedirectUrl.get(0).getTo());
             else if(filterRedirectUrl.get(0).getMoveType()== HttpServletResponse.SC_MOVED_TEMPORARILY)
                Utils.redirect302(request,response,filterRedirectUrl.get(0).getTo());
             else
                 Utils.urlDispatcher(request,response,filterRedirectUrl.get(0).getTo());
        }else{
           chain.doFilter(request, response);
        }
    }
}
