package com.core.accounting.services.impl;

import com.core.accounting.model.contextmodel.*;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.enums.EAccountType;
import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.enums.ETransactionSourceType;
import com.core.accounting.model.enums.EWageType;
import com.core.accounting.model.wrapper.*;
import com.core.accounting.repository.*;
import com.core.accounting.services.*;
import com.core.accounting.services.factory.PaymentServiceFactory;
import com.core.common.model.bankpayment.BankPaymentResponse;
import com.core.common.services.CommonService;
import com.core.common.services.RetryableService;
import com.core.common.services.UserService;
import com.core.common.services.impl.AbstractService;
import com.core.common.util.Utils;
import com.core.datamodel.model.contextmodel.GeneralBankObject;
import com.core.datamodel.model.dbmodel.User;
import com.core.datamodel.model.enums.EBank;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.services.CalendarService;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.util.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service("costSharingServiceImpl")
public class CostSharingServiceImpl extends AbstractService implements CostSharingService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountingService accountingService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private CostShareTypeRepository costShareTypeRepository;
    @Autowired
    private CostShareRequestRepository costShareRequestRepository;
    @Autowired
    private CostShareRequestDetailRepository costShareRequestDetailRepository;
    @Autowired
    private CostDetailRepository costDetailRepository;


    //region CostShareRequest


    @Override
    public CostShareType getCostShareTypeInfo(Long costShareTypeId) {
        if (costShareTypeId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<CostShareType>   result = costShareTypeRepository.findByEntityId(costShareTypeId);

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public List<CostShareType> getAllCostShareTypes() {
        return costShareTypeRepository.findAllCostShareTypes();
    }

    @Override
    public List<CostShareType> getAllActiveCostShareTypes() {
        return costShareTypeRepository.findAllActiveCostShareTypes();
    }

    @Override
    public CostShareRequest getCostShareRequestInfo(Long costShareRequestId) {
        if (costShareRequestId == null)
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.id_required");

        Optional<CostShareRequest> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costShareRequestRepository.findByEntityId(costShareRequestId);
        else
            result = costShareRequestRepository.findByEntityId(costShareRequestId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.costShareRequest.id_notFound");
        return result.get();

    }

    @Override
    public CostShareRequestWrapper getCostShareRequestWrapperInfo(Long costShareRequestId) {
        if (costShareRequestId == null)
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.id_required");

        Optional<CostShareRequestWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costShareRequestRepository.findWrapperById(costShareRequestId);
        else
            result = costShareRequestRepository.findWrapperByIdAndUserId(costShareRequestId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.costShareRequest.id_notFound");
        return result.get();
    }

    @Override
    public List<CostShareRequestWrapper> getCostShareRequestWrappers(Map<String, Object> requestParams) {
        Long userId = Utils.getAsLongFromMap(requestParams, "userId", false, Utils.getCurrentUserId());
        Integer start = Utils.getAsIntegerFromMap(requestParams, "start", false);
        Integer count = Utils.getAsIntegerFromMap(requestParams, "count", false);
        Long typeId = Utils.getAsLongFromMap(requestParams, "typeId", false);

        if (!hasRoleType(ERoleType.ADMIN))
            userId = Utils.getCurrentUserId();
        if (typeId>0)
           return costShareRequestRepository.findWrapperByUserIdAndTypeId(userId,typeId, Utils.gotoPage(start, count));
        else
           return costShareRequestRepository.findWrapperByUserId(userId, Utils.gotoPage(start, count));
    }

    @Transactional
    @Override
    public String createCostShareRequest(CostShareRequestDto costShareRequestDto) {
        CostShareRequest costShareRequest = new CostShareRequest();
        costShareRequest.setUserId(BaseUtils.getCurrentUser().getId());
        costShareRequest = this.mapCostShareRequestDtoToDb(costShareRequestDto, costShareRequest);

        costShareRequest.setCostShareType(this.getCostShareTypeInfo(costShareRequestDto.getTypeId()));
        costShareRequest.setAccount(accountingService.getAccountInfoById(costShareRequestDto.getAccountId()));

        if (costShareRequestDto.getCostShareRequestDetails() != null) {
            for (CostShareRequestDetailDto d : costShareRequestDto.getCostShareRequestDetails()) {
                this.createCostShareRequestDetail(d, costShareRequest);
            }
        }
        costShareRequest = costShareRequestRepository.save(costShareRequest);
        costShareRequestDetailRepository.saveAll(costShareRequest.getCostShareRequestDetails());
        return Utils.getMessageResource("global.operation.success_info");
    }

    private CostShareRequest mapCostShareRequestDtoToDb(CostShareRequestDto costShareRequestDto, CostShareRequest costShareRequest) {
        costShareRequest.setTitle(costShareRequestDto.getTitle());
        costShareRequest.setDescription(costShareRequestDto.getDescription());
        costShareRequest.setActive(costShareRequestDto.getActive());
        costShareRequest.setExpireDate(calendarService.getDateTimeAt24(calendarService.getDateFromString(costShareRequestDto.getExpireDate())));

        if (costShareRequest.getExpireDate().getTime() < (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.expireDate_current_invalid");

        if (costShareRequest.getCostShareRequestDetails()!= null && costShareRequest.getCostShareRequestDetails().stream().filter(d -> d.getDoneDate() != null && d.getDoneDate().getTime() > costShareRequest.getExpireDate().getTime()).count() > 0)
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.expireDate_done_invalid");

        return costShareRequest;
    }


    @Transactional
    @Override
    public String editCostShareRequest(CostShareRequestDto costShareRequestDto) {
        CostShareRequest costShareRequest = this.getCostShareRequestInfo(costShareRequestDto.getId());
        costShareRequest = this.mapCostShareRequestDtoToDb(costShareRequestDto, costShareRequest);
        costShareRequestRepository.save(costShareRequest);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String removeCostShareRequest(Long costShareRequestId) {
        CostShareRequest costShareRequest = this.getCostShareRequestInfo(costShareRequestId);
        if (costShareRequest.getCostShareRequestDetails() != null && costShareRequest.getCostShareRequestDetails().stream().filter(d -> d.getDoneDate() != null).count() > 0)
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.remove_denied");

        costDetailRepository.deleteAll(costShareRequest.getCostDetails());
        costShareRequestDetailRepository.deleteAll(costShareRequest.getCostShareRequestDetails());
        costShareRequestRepository.delete(costShareRequest);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Override
    public CostShareRequestDetail getCostShareRequestDetailInfo(Long costShareRequestDetailId) {
        if (costShareRequestDetailId== null)
            throw new InvalidDataException("Invalid Data", "common.id_required");
        Optional<CostShareRequestDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costShareRequestDetailRepository.findByEntityId(costShareRequestDetailId);
        else
            result = costShareRequestDetailRepository.findByEntityId(costShareRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public CostShareRequestDetail getCostShareRequestDetailInfoForReceiverUser(Long costShareRequestDetailId) {
        if (costShareRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<CostShareRequestDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costShareRequestDetailRepository.findByEntityId(costShareRequestDetailId);
        else
            result = costShareRequestDetailRepository.findByEntityIdAndReceiverId(costShareRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();
    }

    @Override
    public CostShareRequestDetailWrapper getCostShareRequestDetailWrapperInfo(Long costShareRequestDetailId) {
        if (costShareRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<CostShareRequestDetailWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costShareRequestDetailRepository.findWrapperById(costShareRequestDetailId);
        else
            result = costShareRequestDetailRepository.findWrapperByIdAndUserId(costShareRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");
        return result.get();

    }

    @Override
    public MessageBoxWrapper getCostShareRequestDetailMessageWrapperInfo(Long costShareRequestDetailId) {
        if (costShareRequestDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.id_required");

        Optional<MessageBoxWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costShareRequestDetailRepository.findMessageBoxWrappersByCostShareRequestDetailId(costShareRequestDetailId);
        else
            result = costShareRequestDetailRepository.findMessageBoxWrappersByCostShareRequestDetailIdAndUserId(costShareRequestDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.id_notFound");

        if (result.get().getSeenDate() == null && Utils.getCurrentUserId().equals(result.get().getTargetUserId()))
            this.seenCostShareRequestDetail(costShareRequestDetailId);

        return result.get();
    }

    @Transactional
    @Override
    public String addCostShareRequestDetail(CostShareRequestDetailNewDto costShareRequestDetailNewDto) {
        CostShareRequest costShareRequest = this.getCostShareRequestInfo(costShareRequestDetailNewDto.getCostShareRequestId());

        CostShareRequestDetail costShareRequestDetail = this.createCostShareRequestDetail(costShareRequestDetailNewDto, costShareRequest);
        costShareRequestDetailRepository.save(costShareRequestDetail);
        return Utils.getMessageResource("global.operation.success_info");

    }

    private CostShareRequestDetail createCostShareRequestDetail(CostShareRequestDetailDto costShareRequestDetailDto, CostShareRequest costShareRequest) {
        CostShareRequestDetail costShareRequestDetail = new CostShareRequestDetail();
        costShareRequestDetail = this.mapCostShareRequestDetailDtoToDb(costShareRequestDetailDto, costShareRequestDetail);
        User targetUser =userService.getUserInfo(costShareRequestDetailDto.getTargetUser());
        if (Utils.getCurrentUserId().equals(targetUser.getId()))
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.self_invalid");

        costShareRequestDetail.setUserId(userService.getUserInfo(costShareRequestDetailDto.getTargetUser()).getId());
        costShareRequestDetail.setCostShareRequest(costShareRequest);
        final Long targetUserId = costShareRequestDetail.getUserId();
        if (costShareRequest.getCostShareRequestDetails() != null && costShareRequest.getCostShareRequestDetails().stream().filter(d -> d.getUserId().equals(targetUserId)).count() > 0)
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.targetUser_tooMany");

        costShareRequest.getCostShareRequestDetails().add(costShareRequestDetail);
        return costShareRequestDetail;
    }

    private CostShareRequestDetail mapCostShareRequestDetailDtoToDb(CostShareRequestDetailDto costShareRequestDetailDto, CostShareRequestDetail costShareRequestDetail) {
        costShareRequestDetail.setAmount(costShareRequestDetailDto.getAmount());
        return costShareRequestDetail;
    }


    @Transactional
    @Override
    public String editCostShareRequestDetail(CostShareRequestDetailDto costShareRequestDetailDto) {
        CostShareRequestDetail costShareRequestDetail = this.getCostShareRequestDetailInfo(costShareRequestDetailDto.getId());
        if (costShareRequestDetail.getDoneDate() != null)
            throw new InvalidDataException("Invalid Data", "common.costShareRequestDetail.done_hint");

        if (costShareRequestDetail.getCostShareRequest().getExpireDate().getTime() < (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.expireDate_hint");

        costShareRequestDetail = this.mapCostShareRequestDetailDtoToDb(costShareRequestDetailDto, costShareRequestDetail);
        costShareRequestDetailRepository.save(costShareRequestDetail);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String removeCostShareRequestDetail(Long costShareRequestDetailId) {
        CostShareRequestDetail costShareRequestDetail = this.getCostShareRequestDetailInfo(costShareRequestDetailId);
        if (costShareRequestDetail.getDoneDate() != null)
            throw new InvalidDataException("Invalid Data", "common.costShareRequestDetail.done_hint");

        if (costShareRequestDetail.getCostShareRequest().getCostShareRequestDetails().size() == 1) {
            return this.removeCostShareRequest(costShareRequestDetail.getCostShareRequest().getId());
        } else {
            costShareRequestDetailRepository.delete(costShareRequestDetail);
            return Utils.getMessageResource("global.operation.success_info");
        }
    }

    @Transactional
    @Override
    public void seenCostShareRequestDetail(Long costShareRequestDetailId) {
        if (costShareRequestDetailId == null)
            throw new ResourceNotFoundException("Invalid Data", "common.id_required");

        CostShareRequestDetail costShareRequestDetail = this.getCostShareRequestDetailInfoForReceiverUser(costShareRequestDetailId);
        if (costShareRequestDetail.getSeenDate() == null) {
            costShareRequestDetail.setSeenDate(new Date());
            costShareRequestDetailRepository.save(costShareRequestDetail);
        }

    }

    @Transactional
    @Override
    public void doneCostShareRequestDetail(Long costShareRequestDetailId, Double amount) {
        CostShareRequestDetail costShareRequestDetail = this.validatePreDoneCostShareRequestDetail(costShareRequestDetailId, amount);
        costShareRequestDetail.setDoneDate(new Date());
        costShareRequestDetailRepository.save(costShareRequestDetail);
    }

    @Override
    public CostShareRequestDetail validatePreDoneCostShareRequestDetail(Long costShareRequestDetailId, Double amount) {
        CostShareRequestDetail costShareRequestDetail = this.getCostShareRequestDetailInfoForReceiverUser(costShareRequestDetailId);
        if (costShareRequestDetail.getDoneDate() != null)
            throw new InvalidDataException("Invalid Data", "common.costShareRequestDetail.done_older");
        if (costShareRequestDetail.getCostShareRequest().getExpireDate().getTime() < (new Date()).getTime())
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.expireDate_hint");
        if (amount != null && !amount.equals(costShareRequestDetail.getAmount()))
            throw new InvalidDataException("Invalid Data", "common.operationRequest.referenceOperationTypeAmount_invalid");

        return costShareRequestDetail;
    }

    @Override
    public CostDetail getCostDetailInfo(Long costDetailId) {
        if (costDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.costDetail.id_required");

        Optional<CostDetail> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costDetailRepository.findByEntityId(costDetailId);
        else
            result = costDetailRepository.findByEntityId(costDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.costDetail.id_notFound");
        return result.get();
    }

    @Override
    public CostDetailWrapper getCostDetailWrapperInfo(Long costDetailId) {
        if (costDetailId == null)
            throw new InvalidDataException("Invalid Data", "common.costDetail.id_required");

        Optional<CostDetailWrapper> result;
        if (hasRoleType(ERoleType.ADMIN))
            result = costDetailRepository.findWrapperById(costDetailId);
        else
            result = costDetailRepository.findWrapperByIdAndUserId(costDetailId, Utils.getCurrentUserId());

        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.costDetail.id_notFound");
        return result.get();
    }

    @Override
    public List<CostDetailWrapper> getCostDetailWrappers(Long costShareRequestId) {
        return this.getCostDetailWrappers(costShareRequestId,true);
    }

    @Override
    public List<CostDetailWrapper> getCostDetailWrappers(Long costShareRequestId, Boolean forceOwner) {
        if (costShareRequestId == null)
            throw new InvalidDataException("Invalid Data", "common.costShareRequest.id_required");

        if (hasRoleType(ERoleType.ADMIN) || !forceOwner)
            return costDetailRepository.findWrapperByCostShareRequestId(costShareRequestId);
        else
            return costDetailRepository.findWrapperByCostShareRequestIdAndUserId(costShareRequestId, Utils.getCurrentUserId());
    }

    @Transactional
    @Override
    public String createCostDetail(CostDetailDto costDetailDto) {
        CostDetail costDetail = new CostDetail();
        CostShareRequest costShareRequest = this.getCostShareRequestInfo(costDetailDto.getCostShareRequestId());
        costDetail.setCostShareRequest(costShareRequest);
        costDetail = this.mapCostDetailDtoToDb(costDetailDto, costDetail);
        costDetailRepository.save(costDetail);
        return Utils.getMessageResource("global.operation.success_info");
    }

    private CostDetail mapCostDetailDtoToDb(CostDetailDto costDetailDto, CostDetail costDetail) {
        costDetail.setDescription(costDetailDto.getDescription());
        costDetail.setAmount(costDetailDto.getAmount());

        if(!Utils.isStringSafeEmpty(costDetailDto.getEffectiveDate()))
           costDetail.setEffectiveDate(calendarService.getDateTimeAt24(calendarService.getDateFromString(costDetailDto.getEffectiveDate())));
        return costDetail;
    }


    @Transactional
    @Override
    public String editCostDetail(CostDetailDto costDetailDto) {
        CostDetail costDetail = this.getCostDetailInfo(costDetailDto.getId());
        costDetail = this.mapCostDetailDtoToDb(costDetailDto, costDetail);
        costDetailRepository.save(costDetail);
        return Utils.getMessageResource("global.operation.success_info");
    }

    @Transactional
    @Override
    public String removeCostDetail(Long costDetailId) {
        CostDetail costDetail = this.getCostDetailInfo(costDetailId);

        costDetailRepository.delete(costDetail);
        return Utils.getMessageResource("global.operation.success_info");

    }


    //endregion CostShareRequest

}
