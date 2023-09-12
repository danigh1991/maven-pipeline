package com.core.card.restcontrollers;


import com.core.card.model.contextmodel.BankCardHolderInquiryDto;
import com.core.card.model.contextmodel.BankCardTransferDto;
import com.core.card.model.contextmodel.OtpDeliveryDto;
import com.core.card.model.view.CardJsonView;
import com.core.card.services.CardService;
import com.core.common.util.Utils;
import com.core.card.model.contextmodel.BankCardDto;
import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.CreateValidationGroup;
import com.core.common.util.validator.EditValidationGroup;
import com.core.responsemodel.ResponseMessage;
import com.core.restcontrollers.AbstractRestController;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/card", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankCardRestController extends AbstractRestController {
    @Autowired
    private CardService cardService;


    //#region  Create Bank  Cart

    @JsonView(CardJsonView.BankCardDetails.class)
    @RequestMapping(value = "/getBankCardWrapperInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCardWrapperInfo(@RequestParam Long bankCardId) {
        return Utils.returnSucsess(cardService.getBankCardWrapperInfo(bankCardId));
    }

    @JsonView(CardJsonView.BankCardList.class)
    @RequestMapping(value = "/getMyBankCardWrappers", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getMyBankCardWrappers(@RequestParam(required =false) Integer type) {
        return Utils.returnSucsess(cardService.getMyBankCardWrappers(type));
    }

    @JsonView(CardJsonView.BankCardList.class)
    @RequestMapping(value = "/getBankCardWrappers", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCardWrappers(@RequestParam Long userId, @RequestParam(required =false) Integer type) {
        return Utils.returnSucsess(cardService.getBankCardWrappers(userId,type));
    }

    @JsonView(CardJsonView.BankCardDetails.class)
    @RequestMapping(value = "/getBankCardInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCardInfo(@RequestParam Long bankCardId) {
        return Utils.returnSucsess(cardService.getBankCardInfo(bankCardId));
    }

    @JsonView(CardJsonView.BankCardList.class)
    @RequestMapping(value = "/getMyBankCards", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getMyBankCards(@RequestParam(required =false)  Integer type) {
        return Utils.returnSucsess(cardService.getMyBankCards(type));
    }

    @JsonView(CardJsonView.BankCardList.class)
    @RequestMapping(value = "/getBankCards", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCards(@RequestParam Long userId,@RequestParam(required =false) Integer type) {
        return Utils.returnSucsess(cardService.getBankCards(userId,type));
    }


    @RequestMapping(value = "/createBankCard", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_bank_card')")
    public ResponseMessage createBankCard(@RequestBody @Validated(CreateValidationGroup.class) BankCardDto bankCardDto) {
        return Utils.returnSucsess(cardService.createBankCard(bankCardDto));
    }

    @RequestMapping(value = "/editBankCard", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_bank_card')")
    public ResponseMessage editBankCard(@RequestBody @Validated(EditValidationGroup.class) BankCardDto bankCardDto) {
        return Utils.returnSucsess(cardService.editBankCard(bankCardDto));
    }

    @RequestMapping(value = "/deleteBankCard", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('delete_bank_card')")
    public ResponseMessage deleteBankCard( @RequestParam Long bankCardId) {
        return Utils.returnSucsess(cardService.deleteBankCard(bankCardId));
    }

    //#endregion Create Bank Cart

    //#region Card Operation

    @JsonView(CardJsonView.BankCardOperationWrapperList.class)
    @RequestMapping(value = "/getBankCardOperationWrappers", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCardOperationWrappers(@RequestParam(required = false) Map<String, Object> requestParams) {
        return Utils.returnSucsess(cardService.getBankCardOperationWrappers(requestParams));
    }

    @JsonView(CardJsonView.BankCardOperationWrapperListAdmin.class)
    @RequestMapping(value = "/getBankCardOperationWrappersForAdmin", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCardOperationWrappersForAdmin(@RequestParam(required = false) Map<String, Object> requestParams) {
        return Utils.returnSucsess(cardService.getBankCardOperationWrappers(requestParams));
    }

    @JsonView(CardJsonView.BankCardOperationWrapperDetail.class)
    @RequestMapping(value = "/getBankCardOperationWrapperInfo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getBankCardOperationWrapperInfo(@RequestParam Long bankCardOperationId) {
        return Utils.returnSucsess(cardService.getBankCardOperationWrapperInfo(bankCardOperationId));
    }

    @JsonView(CardJsonView.BankCardOperationWrapperDetailAdmin.class)
    @RequestMapping(value = "/getBankCardOperationWrapperInfoForAdmin", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('admin_view_bank_card')")
    public ResponseMessage getBankCardOperationWrapperInfoForAdmin(@RequestParam Long bankCardOperationId) {
        return Utils.returnSucsess(cardService.getBankCardOperationWrapperInfo(bankCardOperationId));
    }

    @RequestMapping(value = "/getCardOperatorStatuses", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage getOperators() {
        return Utils.returnSucsess(cardService.getCardOperatorStatuses());
    }



    //#endregion Card Operation


    //#region SHP Card Operation

    @JsonView(CardJsonView.CardEnrollmentResponseView.class)
    @RequestMapping(value = "/createBankCardOnShp", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_bank_card')")
    public ResponseMessage createBankCardOnShp(@RequestBody Map<String,String> input) {
        return Utils.returnSucsess(cardService.cardEnrollment(Utils.cleanArabicAndHtmlAndXss(input.get("platform"))));
    }

    @RequestMapping(value = "/approvedCardEnrollment", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_bank_card')")
    public ResponseMessage approvedCardEnrollment(@RequestBody Map<String,String> input) {
        return Utils.returnSucsess(cardService.approvedCardEnrollment(Utils.cleanArabicAndHtmlAndXss(input.get("trackingId"))));
    }

    @RequestMapping(value = "/renewCardId", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_bank_card')")
    public ResponseMessage renewCardId(@RequestBody Map<String,Long>  input) {
        return Utils.returnSucsess(cardService.renewCardId(input.get("bankCardId")));
    }

    @JsonView(CardJsonView.AppReactivationResponseView.class)
    @RequestMapping(value = "/appReactivation", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('view_bank_card')")
    public ResponseMessage appReactivation(@RequestBody Map<String,String> input) {
        return Utils.returnSucsess(cardService.appReactivation(Utils.cleanArabicAndHtmlAndXss(input.get("platform"))));
    }


    @JsonView(CardJsonView.CardHolderInquiryResponseView.class)
    @RequestMapping(value = "/cardHolderInquiry", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('transfer_bank_card')")
    public ResponseMessage cardHolderInquiry(@RequestBody @Validated(CreateValidationGroup.class) BankCardHolderInquiryDto bankCardHolderInquiryDto, HttpServletRequest request) {
        return Utils.returnSucsess(cardService.cardHolderInquiry(bankCardHolderInquiryDto,request));
    }

    @JsonView(CardJsonView.CardTransferResponseView.class)
    @RequestMapping(value = "/cardTransfer", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('transfer_bank_card')")
    public ResponseMessage cardTransfer(@RequestBody @Validated(CreateValidationGroup.class) BankCardTransferDto bankCardTransferDto, HttpServletRequest request) {
        return Utils.returnSucsess(cardService.cardTransfer(bankCardTransferDto,request));
    }

    @RequestMapping(value = "/getShpOtpRemainTime", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('otp_bank_card')")
    public ResponseMessage getShpOtpRemainTime() {
        return Utils.returnSucsess(cardService.getShpOtpRemainTime());
    }

    @RequestMapping(value = "/requestOtp", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('otp_bank_card')")
    public ResponseMessage requestOtp(@RequestBody @Validated(CreateValidationGroup.class) OtpDeliveryDto otpDeliveryDto, HttpServletRequest request) {
        return Utils.returnSucsess(cardService.requestOtp(otpDeliveryDto,request));
    }

    //#endregion SHP Card Operation
}
