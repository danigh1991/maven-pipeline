package com.core.topup.services.impl;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.services.CacheService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.topup.model.contextmodel.TopUpReserveRequestDto;
import com.core.topup.model.dbmodel.TopUpRequest;
import com.core.topup.model.enums.EOperator;
import com.core.topup.model.enums.ETopUpRequestStatus;
import com.core.topup.model.enums.ETopUpType;
import com.core.topup.model.topupmodel.TopUpReserveResponse;
import com.core.topup.model.topupmodel.TopUpSaleResponse;
import com.core.topup.model.topupmodel.TopUpStatusResponse;
import com.core.topup.repository.TopUpRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component("topUpUtil")
public class TopUpUtil {

    private CacheService cacheService;
    private CommonService commonService;
    private TopUpRequestRepository topUpRequestRepository;

    @Autowired
    public TopUpUtil(CacheService cacheService,CommonService commonService,TopUpRequestRepository topUpRequestRepository) {
        this.cacheService = cacheService;
        this.commonService = commonService;
        this.topUpRequestRepository = topUpRequestRepository;
    }

    public Boolean hasExistByOperationRequestReferenceNumber(String referenceNumber) {
        return topUpRequestRepository.existByOperationRequestReferenceNumber(referenceNumber);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TopUpRequest createTopUpRequest(TopUpReserveRequestDto topUpReserveRequestDto,String description, ETopUpType eTopUpType, HttpServletRequest request){
        TopUpRequest topUpRequest=new TopUpRequest();
        EOperator eOperator= EOperator.valueOf(topUpReserveRequestDto.getOperatorId());

        topUpRequest.setTypeId(eTopUpType.getId());
        if(eTopUpType==ETopUpType.CHARGE) {
            topUpRequest.setChargeType(topUpReserveRequestDto.getChargeType());
            topUpRequest.setProductTypeId(topUpReserveRequestDto.getProductTypeId());
            topUpRequest.setProductId(topUpReserveRequestDto.getProductId());
        }else if(eTopUpType==ETopUpType.INTERNET_PACKAGE){
            topUpRequest.setPackageType(topUpReserveRequestDto.getPackageType());
        }else{
            throw new InvalidDataException("Invalid Data", "common.topUp.type_invalid");
        }
        topUpRequest.setDescription(description);
        topUpRequest.setAmount(topUpReserveRequestDto.getAmount());
        if (!Utils.isValidByPattern(topUpReserveRequestDto.getPhoneNumber(), commonService.getMobilePattern()))
            throw new InvalidDataException("InvalidData", "common.topUp.phoneNumber_len");
        topUpRequest.setPhoneNumber(topUpReserveRequestDto.getPhoneNumber());
        topUpRequest.setOperator(eOperator.name());
        topUpRequest.setSaleDescription(commonService.getMyBrandName());
        topUpRequest.setTrackingId(Utils.createUniqueRandom());

        topUpRequest.setCardNo(Utils.getCurrentUser().getMobileNumber());
        topUpRequest.setCardType("9");
        topUpRequest.setChannelId("16");

        //topUpRequest.setOperationRequestId();
        topUpRequest.setRequestIp(Utils.getClientFirstIp(request));
        topUpRequest.setUserId(Utils.getCurrentUserId());
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TopUpRequest successReserveResultInTopUpRequest(TopUpRequest topUpRequest, TopUpReserveResponse topUpReserveResponse){
        topUpRequest.setResponse(Utils.getAsJson(topUpReserveResponse));
        topUpRequest.setReserveId(topUpReserveResponse.getSaleId());
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TopUpRequest successSaleResultInTopUpRequest(Long topUpRequestId, TopUpSaleResponse topUpSaleResponse){
        TopUpRequest topUpRequest=this.getTopUpRequestInfo(topUpRequestId);
        topUpRequest.setOperatorTransactionId(topUpSaleResponse.getTransactionId());
        topUpRequest.setSaleId(topUpSaleResponse.getSaleId());
        topUpRequest.setResponse(Utils.getAsJson(topUpSaleResponse));
        topUpRequest.setStatus(1);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TopUpRequest failResultInReserveTopUpRequestResult(TopUpRequest topUpRequest, String response){
        topUpRequest.setResponse(response);
        topUpRequest.setStatus(2);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TopUpRequest failResultInSaleTopUpRequestResult(TopUpRequest topUpRequest, TopUpSaleResponse topUpSaleResponse,Boolean unknownState){
        topUpRequest.setResponse(Utils.getAsJson(topUpSaleResponse));
        topUpRequest.setStatus(unknownState ? 3 : 2);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    @Transactional
    public TopUpRequest failResultInStatusTopUpRequestResult(TopUpRequest topUpRequest,Integer status,  String response){
        topUpRequest.setResponse(response);
        topUpRequest.setStatus(status);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    @Transactional
    public TopUpRequest successStatusTopUpRequestResult(TopUpRequest topUpRequest, TopUpStatusResponse topUpStatusResponse ){
        topUpRequest.setOperatorTransactionId(topUpStatusResponse.getTransactionId());
        topUpRequest.setResponse(Utils.getAsJson(topUpStatusResponse));
        topUpRequest.setStatus(1);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    @Transactional
    public TopUpRequest markToManualControl(TopUpRequest topUpRequest,  String response){
        topUpRequest.setResponse(response);
        topUpRequest.setStatus(4);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }



    @Transactional
    public TopUpRequest setOperationRequestIdInTopUpRequest(Long topUpRequestId, Long operationRequestId){
        TopUpRequest topUpRequest=this.getTopUpRequestInfo(topUpRequestId);
        topUpRequest.setOperationRequestId(operationRequestId);
        topUpRequest=topUpRequestRepository.save(topUpRequest);
        return  topUpRequest;
    }

    public TopUpRequest getTopUpRequestInfo(Long topUpRequestId){
        Optional<TopUpRequest> result;
        //if (Utils.hasRoleType(ERoleType.ADMIN))
            result= topUpRequestRepository.findByEntityId(topUpRequestId);
        //else
            //result= topUpRequestRepository.findByEntityId(topUpRequestId,Utils.getCurrentUserId());
        if (!result.isPresent())
            throw new ResourceNotFoundException(topUpRequestId.toString(), "common.topUpRequest.id_notFound" );
        return result.get();
    }

    public TopUpRequest getTopUpRequestInfoByOperationRequestId(Long operationRequestId){
        Optional<TopUpRequest> result;
        result= topUpRequestRepository.findByOperationRequestId(operationRequestId);
        if (!result.isPresent())
            throw new ResourceNotFoundException(operationRequestId.toString(), "common.topUpRequest.operationRequestId_notFound" );
        return result.get();
    }

    public TopUpRequest getTopUpRequestInfoByReferenceNumber(String referenceNumber){
        Optional<TopUpRequest> result;
        result= topUpRequestRepository.findByReferenceNumber(referenceNumber);
        if (!result.isPresent())
            throw new ResourceNotFoundException(referenceNumber.toString(), "common.topUpRequest.referenceNumber_notFound" );
        return result.get();
    }

    public List<TopUpRequest> getUnknownStatusTopUpRequest(){
        return topUpRequestRepository.findByState(ETopUpRequestStatus.PENDING.getId(),Utils.addSecondToDate(new Date(),-15),Utils.gotoPage(0,30));
    }

}
