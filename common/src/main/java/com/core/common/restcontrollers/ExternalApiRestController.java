package com.core.common.restcontrollers;


import com.core.common.externalapi.ExternalApiUtil;
import com.core.common.util.Utils;
import com.core.datamodel.model.view.MyJsonView;
import com.core.responsemodel.ResponseMessage;
import com.core.restcontrollers.AbstractRestController;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/externalApi", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExternalApiRestController extends AbstractRestController {

    private ExternalApiUtil externalApiUtil;

    @Autowired
    public ExternalApiRestController(ExternalApiUtil externalApiUtil) {
        this.externalApiUtil = externalApiUtil;
    }

    //region ExternalApi

    @RequestMapping(value = "/getAllExternalApis", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_external_api')")
    @JsonView(MyJsonView.ExternalApiList.class)
    public ResponseMessage getAllExternalApis() {
        return Utils.returnSucsess(externalApiUtil.getAllExternalApis());
    }

    @RequestMapping(value = "/getExternalApiInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_external_api')")
    @JsonView(MyJsonView.ExternalApiDetails.class)
    public ResponseMessage getExternalApiInfo(@RequestParam Long externalApiId) {
        return Utils.returnSucsess(externalApiUtil.getExternalApiInfo(externalApiId));
    }

    //endregion ExternalApi

    //region ExternalApiCall

    @JsonView(MyJsonView.ExternalApiCallList.class)
    @RequestMapping(value = "/getExternalApiCallWrappers", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_external_api_call')")
    public ResponseMessage getExternalApiCallWrappers(@RequestParam(required = false) Map<String, Object> requestParams) {
        return Utils.returnSucsess(externalApiUtil.getExternalApiCallWrappers(requestParams));
    }

    @JsonView(MyJsonView.ExternalApiCallDetails.class)
    @RequestMapping(value = "/getExternalApiCallWrapperInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_external_api_call')")
    public ResponseMessage getExternalApiCallWrapperInfo(@RequestParam Long externalApiCallId) {
        return Utils.returnSucsess(externalApiUtil.getExternalApiCallWrapperInfo(externalApiCallId));
    }


    @JsonView(MyJsonView.TypeWrapperList.class)
    @RequestMapping(value = "/getExternalApiCallStatuses", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_external_api_call')")
    public ResponseMessage getExternalApiCallStatuses() {
        return Utils.returnSucsess(externalApiUtil.getExternalApiCallStatuses());
    }

    //endregion ExternalApiCall

}
