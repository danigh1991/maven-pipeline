package com.core.common.restcontrollers;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.view.MyJsonView;
import com.core.responsemodel.ResponseMessage;
import com.core.restcontrollers.AbstractRestController;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@RequestMapping(value = "/api/shareInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShareInfoRestController extends AbstractRestController {
    @Autowired
    private CommonService commonService;

    @JsonView(MyJsonView.CityView.class)
    @RequestMapping(value = "/getCityList", method = RequestMethod.GET)
    public ResponseMessage getCityList() {
        return Utils.returnSucsess(commonService.getCityList());
    }

    @JsonView(MyJsonView.RegionView.class)
    @RequestMapping(value = "/getCityRegionList", method = RequestMethod.GET)
    public ResponseMessage getRegionList(@RequestParam Long cityId) {
        return Utils.returnSucsess(commonService.getRegionList(cityId));
    }
}
