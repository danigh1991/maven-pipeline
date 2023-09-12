package com.core.topup.restcontrollers;


import com.core.common.util.Utils;
import com.core.responsemodel.ResponseMessage;
import com.core.restcontrollers.AbstractRestController;
import com.core.topup.model.contextmodel.TopUpReserveRequestDto;
import com.core.topup.model.enums.EOperator;
import com.core.topup.model.view.TopUpJsonView;
import com.core.topup.services.TopUpService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/topup", produces = MediaType.APPLICATION_JSON_VALUE)
public class TopUpRestController extends AbstractRestController {

    private TopUpService topUpService;

    @Autowired
    public TopUpRestController(TopUpService topUpService) {
        this.topUpService = topUpService;
    }

/*    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_get_token')")
    public ResponseMessage getToken() {
        return Utils.returnSucsess(topUpService.getToken(false,null));
    }*/

    @RequestMapping(value = "/getOperators", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getOperators() {
        return Utils.returnSucsess(topUpService.getOperators());
    }

    @RequestMapping(value = "/getTopUpRequestStatuses", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getTopUpRequestStatuses() {
        return Utils.returnSucsess(topUpService.getTopUpRequestStatuses());
    }


    @RequestMapping(value = "/getAllDirectChargeType", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getAllDirectChargeType() {
        return Utils.returnSucsess(topUpService.getAllDirectChargeType());
    }

    @RequestMapping(value = "/getAllInternetPackageData", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getAllInternetPackageData() {
        return Utils.returnSucsess(topUpService.getAllInternetPackageData());
    }

    @RequestMapping(value = "/getDirectChargeType", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getDirectChargeType(@RequestParam Integer operatorId) {
        return Utils.returnSucsess(topUpService.getDirectChargeType(EOperator.valueOf(operatorId)));
    }


    @RequestMapping(value = "/reserveAndSale", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_topup_charge')")
    public ResponseMessage reserveAndSaleCharge(HttpServletRequest request,@Valid @RequestBody TopUpReserveRequestDto topUpReserveRequestDto) {
        return Utils.returnSucsess(topUpService.reserveAndSale(topUpReserveRequestDto,request));
    }

//    @PreAuthorize("hasAuthority('create_internet_package')")

    @RequestMapping(value = "/getMciProductList", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getMciProductList(@RequestParam Integer productTypeId) {
        return Utils.returnSucsess(topUpService.getMciProductList(productTypeId));
    }

    @RequestMapping(value = "/getInternetPackageList", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_base_data')")
    public ResponseMessage getInternetPackageList(@RequestParam Integer operatorId) {
        return Utils.returnSucsess(topUpService.getInternetPackageList(EOperator.valueOf(operatorId)));
    }


    @JsonView(TopUpJsonView.TopUpRequestWrapperList.class)
    @RequestMapping(value = "/getTopUpRequestWrappers", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_data')")
    public ResponseMessage getTopUpRequestWrappers(@RequestParam(required = false) Map<String, Object> requestParams) {
        return Utils.returnSucsess(topUpService.getTopUpRequestWrappers(requestParams));
    }

    @JsonView(TopUpJsonView.TopUpRequestWrapperDetail.class)
    @RequestMapping(value = "/getTopUpRequestWrapperInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('topup_view_data')")
    public ResponseMessage getTopUpRequestWrapperInfo(@RequestParam Long topUpRequestId) {
        return Utils.returnSucsess(topUpService.getTopUpRequestWrapperInfo(topUpRequestId));
    }



}
