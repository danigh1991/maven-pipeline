package com.core.accounting.services.impl;

import com.core.accounting.model.contextmodel.MerchantDto;
import com.core.accounting.model.dbmodel.*;
import com.core.accounting.model.enums.EMerchantViewPolicy;
import com.core.accounting.model.wrapper.MerchantCustomerWrapper;
import com.core.accounting.model.wrapper.MerchantWrapper;
import com.core.accounting.repository.*;
import com.core.accounting.services.MerchantService;
import com.core.common.services.CommonService;
import com.core.common.services.UserService;
import com.core.common.services.impl.AbstractService;
import com.core.common.services.impl.DynamicQueryHelper;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.City;
import com.core.datamodel.model.dbmodel.Region;
import com.core.datamodel.model.enums.ERoleType;
import com.core.exception.InvalidDataException;
import com.core.exception.ResourceNotFoundException;
import com.core.accounting.model.enums.EMerchantCategoryCode;
import com.core.model.wrapper.ResultListPageable;
import com.core.model.wrapper.TypeWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("merchantServiceImpl")
public class MerchantServiceImpl extends AbstractService implements MerchantService {

    @Value("#{${merchant.search.native.private.params}}")
    private HashMap<String, List<String>> MERCHANT_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;

    @Value("#{${merchant.customer.search.native.private.params}}")
    private HashMap<String, List<String>> MERCHANT_CUSTOMER_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS;

    private UserService userService;
    private CommonService commonService;
    private MerchantCategoryRepository merchantCategoryRepository;
    private MerchantRepository merchantRepository;
    private MerchantCustomerRepository merchantCustomerRepository;
    private DynamicQueryHelper dynamicQueryHelper;

    @Autowired
    public MerchantServiceImpl(UserService userService, CommonService commonService,MerchantCategoryRepository merchantCategoryRepository,
                               MerchantRepository merchantRepository,MerchantCustomerRepository merchantCustomerRepository,DynamicQueryHelper dynamicQueryHelper) {
        this.userService = userService;
        this.commonService = commonService;
        this.merchantCategoryRepository = merchantCategoryRepository;
        this.merchantRepository = merchantRepository;
        this.merchantCustomerRepository = merchantCustomerRepository;
        this.dynamicQueryHelper = dynamicQueryHelper;
    }

    //region  MerchantCategory

    @Override
    public MerchantCategory getMerchantCategoryInfo(Long merchantCategoryId) {
        if (merchantCategoryId == null)
            throw new InvalidDataException("Invalid Data", "common.merchantCategory.id_required");

        Optional<MerchantCategory> result = merchantCategoryRepository.findByEntityId(merchantCategoryId);
        if (!result.isPresent())
            throw new ResourceNotFoundException("Invalid Data", "common.merchantCategory.id_notFound");
        return result.get();
    }

    @Override
    public List<MerchantCategory> getAllMerchantCategories() {
        return merchantCategoryRepository.findAllMerchantCategories();
    }

    @Override
    public List<MerchantCategory> getAllActiveMerchantCategories() {
        return merchantCategoryRepository.findAllActiveMerchantCategories();
    }


    //endregion MerchantCategory


    //#region Merchant

    @Override
    public MerchantWrapper getMerchantWrapperInfo(String userName) {
        if(Utils.isStringSafeEmpty(userName))
            throw new InvalidDataException("Invalid Data", "common.user.username_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("userName",userName);
        requestParams.put("active",true);
        requestParams.put("resultSetMapping", "merchantWrapperMapping");

        ResultListPageable<MerchantWrapper> result=this.getMerchantGeneral(requestParams,false);
        if (result.getResult() != null && result.getResult().size() > 0)
            return result.getResult().get(0);
        throw new ResourceNotFoundException("Not Found", "common.merchant.id_notFound");
    }

    @Override
    public ResultListPageable<MerchantWrapper> getMerchantWrappersByCategoryCode(Integer categoryCode) {
        if(categoryCode==null)
            throw new InvalidDataException("Invalid Data", "common.merchantCategory.code_required");
        Map<String, Object> requestParams=new HashMap<>();
        requestParams.put("merchantCategoryCode",categoryCode);
        requestParams.put("active",true);

        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="name=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(MERCHANT_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        requestParams.put("resultSetMapping", "merchantWrapperMapping");
        ResultListPageable<MerchantWrapper> merchantResultListPageable=this.getMerchantGeneral(requestParams,false);
        return merchantResultListPageable;
    }

    @Override
    public ResultListPageable<MerchantWrapper> getAllMerchants(Map<String, Object> requestParams ) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(MERCHANT_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);

        //Show Other Merchants
        Boolean userLimit=true;
        if(!hasRoleType(ERoleType.ADMIN) && this.isActiveMerchant(Utils.getCurrentUserId()))
            userLimit=this.getOtherMerchantViewPolicyByUserId(Utils.getCurrentUserId())==EMerchantViewPolicy.ALL ? false : true;

        requestParams.put("resultSetMapping", "merchantWrapperMapping");
        ResultListPageable<MerchantWrapper> merchantResultListPageable=this.getMerchantGeneral(requestParams,userLimit);
        return merchantResultListPageable;
    }

    private String getBaseQueryHead(Map<String, Object> requestParams ) {
        Boolean loadDetails=Utils.getAsBooleanFromMap(requestParams,"loadDetails",false,false);
        return "select  m.mrc_id as id,mg.mrg_id as merchantCategoryId, mg.mrg_name as merchantCategoryName, mg.mrg_code as merchantCategoryCode ,  m.mrc_usr_id as userId, u.usr_username as userName, \n " +
                "       m.mrc_name as name, m.mrc_description as description,  m.mrc_active as active,   m.mrc_address as address, m.mrc_lat as lat,   m.mrc_lan as lan, m.mrc_postal_code as postalCode, \n" +
                "       m.mrc_email as email, m.mrc_mobile_number as mobileNumber, m.mrc_phone_number as phoneNumber,  m.mrc_cty_id as cityId, c.cty_name as cityName, m.mrc_crg_id as regionId,\n" +
                "       r.crg_name as regionName, m.mrc_wallet as wallet, m.mrc_card as card, m.mrc_card_number as cardNumber, m.mrc_other_merchant_view_policy as otherMerchantViewPolicy , \n"+
                "       m.mrc_create_by as createBy,  m.mrc_create_date as createDate, m.mrc_modify_by as modifyBy,  m.mrc_modify_date as modifyDate\n" ;
    }
    private String getBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getBaseQueryBody(Map<String, Object> requestParams) {
        String queryString="   from sc_merchant m \n" +
                           "  inner join sc_merchant_category mg on (m.mrc_mrg_id=mg.mrg_id)\n" +
                           "  inner join sc_user u on (m.mrc_usr_id=u.usr_id)\n" +
                           "  inner join sc_city c on (m.mrc_cty_id=c.cty_id)\n" +
                           "  inner join sc_city_region r on (c.cty_id=r.crg_cty_id and m.mrc_crg_id=r.crg_id) \n" ;
        return  queryString;
    }

    private ResultListPageable<MerchantWrapper> getMerchantGeneral(Map<String, Object> requestParams,Boolean userLimit) {
        String queryString = this.getBaseQueryHead(requestParams)+ this.getBaseQueryBody(requestParams);
        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getBaseCountQueryHead(requestParams)+this.getBaseQueryBody(requestParams);
        return dynamicQueryHelper.getDataGeneral(requestParams,MERCHANT_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,MerchantWrapper.class,userLimit);
    }

    @Override
    public Merchant getMerchantInfo(Long merchantId) {
        if( merchantId==null)
            throw new InvalidDataException("Invalid Data", "common.merchant.id_required");
        Optional<Merchant> merchant=null;
        if (hasRoleType(ERoleType.ADMIN))
            merchant = merchantRepository.findByEntityId(merchantId);
        else
            merchant = merchantRepository.findByEntityId(merchantId,Utils.getCurrentUserId());

        if(!merchant.isPresent())
            throw new InvalidDataException("Invalid Data", "common.merchant.notFount");
        return merchant.get();
    }

    @Override
    public Merchant getActiveMerchantInfo(Long merchantId) {
        if( merchantId==null)
            throw new InvalidDataException("Invalid Data", "common.merchant.id_required");
        Optional<Merchant> merchant=null;
        if (hasRoleType(ERoleType.ADMIN))
            merchant = merchantRepository.findActiveByEntityId(merchantId);
        else
            merchant = merchantRepository.findActiveByEntityId(merchantId,Utils.getCurrentUserId());

        if(!merchant.isPresent())
            throw new InvalidDataException("Invalid Data", "common.merchant.notFount");
        return merchant.get();
    }

    @Override
    public Merchant getMerchantInfoByUserId(Long userId) {
        if( userId==null)
            throw new InvalidDataException("Invalid Data", "common.user.id_required");
        Optional<Merchant> merchant= merchantRepository.findByUserId(userId);
        if(!merchant.isPresent())
            throw new InvalidDataException("Invalid Data", "common.merchant.notFount");
        return merchant.get();
    }

    @Override
    public Merchant getActiveMerchantInfoByUserId(Long userId) {
        if(userId==null)
            throw new InvalidDataException("Invalid Data", "common.user.id_required");
        Optional<Merchant> merchant= merchantRepository.findActiveByUserId(userId);
        if(!merchant.isPresent())
            throw new InvalidDataException("Invalid Data", "common.merchant.notFount");
        return merchant.get();
    }


    @Transactional
    @Override
    public Merchant addEditMerchant(MerchantDto merchantDto) {
        Merchant merchant=null;
        if (!hasRoleType(ERoleType.ADMIN) || merchantDto.getUserId()==null)
            merchantDto.setUserId(Utils.getCurrentUserId());

        try {
            if(merchantDto.getId()!=null)
                merchant=this.getMerchantInfo(merchantDto.getId());
            else
               merchant=this.getMerchantInfoByUserId(merchantDto.getUserId());
        }catch (InvalidDataException ex){}

        if(merchant==null) {
            if(merchantRepository.countByUserId(merchantDto.getUserId())>0)
                throw new InvalidDataException("InvalidData", "common.merchant.exist");
            merchant = new Merchant();
            merchant.setUserId(merchantDto.getUserId());
            merchant.setActive(false);
        }

        MerchantCategory merchantCategory=this.getMerchantCategoryInfo(merchantDto.getMerchantCategoryId());
        EMerchantCategoryCode.valueOf(merchantCategory.getCode());
        merchant.setMerchantCategory(merchantCategory);

        City city=commonService.getActiveCityInfo(merchantDto.getCityId());
        Region region=commonService.getRegionByIdAndCityId(merchantDto.getCityId(),merchantDto.getRegionId());

        merchant.setName(merchantDto.getName());
        merchant.setDescription(merchantDto.getDescription());
        merchant.setAddress(merchantDto.getAddress());
        merchant.setEmail(merchantDto.getEmail());
        if(!Utils.isValidByPattern(merchantDto.getMobileNumber(), commonService.getMobilePattern()))
            throw new InvalidDataException("InvalidData", "common.user.mobileNumber_len");
        merchant.setMobileNumber(merchantDto.getMobileNumber());
        merchant.setPhoneNumber(merchantDto.getPhoneNumber());

        if(!Utils.isValidByPattern(merchantDto.getPostalCode(), city.getPostalCodePattern()))
            throw new InvalidDataException("InvalidData", "common.address.postalCode_invalid");
        merchant.setPostalCode(merchantDto.getPostalCode());
        merchant.setLat(merchantDto.getLat());
        merchant.setLan(merchantDto.getLan());

        merchant.setCityId(merchantDto.getCityId());
        merchant.setRegionId(merchantDto.getRegionId());
        merchant.setWallet(merchantDto.getWallet());
        merchant.setCard(merchantDto.getCard());
        if(merchantDto.getCard() && Utils.isStringSafeEmpty(merchantDto.getCardNumber()))
            throw new InvalidDataException("InvalidData", "common.merchant.cardNumber_required");
        if(!Utils.isStringSafeEmpty(merchantDto.getCardNumber()) && !Utils.validateBankCardNumber(merchantDto.getCardNumber()))
           throw new InvalidDataException("InvalidData", "common.merchant.cardNumber_invalid");
        merchant.setCardNumber(merchantDto.getCardNumber());
        merchant.setOtherMerchantViewPolicy(EMerchantViewPolicy.valueOf(merchantDto.getOtherMerchantViewPolicy()).getId());
        merchant=merchantRepository.save(merchant);
        return merchant;
    }

    @Transactional
    @Override
    public Boolean changeMerchantState(Long merchantId) {
        Merchant merchant=this.getMerchantInfo(merchantId);
        merchant.setActive(!merchant.getActive());
        merchant=merchantRepository.save(merchant);
        return merchant.getActive();
    }

    @Override
    public Boolean isMerchant(Long userId) {
        return merchantRepository.countByUserId(userId)>0;
    }

    @Override
    public Boolean isActiveMerchant(Long userId) {
        return merchantRepository.countActiveByUserId(userId)>0;
    }

    @Override
    public Integer getMerchantTransferOperationTypeCode(Long userId) {
        return merchantRepository.findMerchantTransferOperationTypeCode(userId);
    }

    @Override
    public EMerchantViewPolicy getOtherMerchantViewPolicyByUserId(Long userId) {
        return EMerchantViewPolicy.valueOf(merchantRepository.findOtherMerchantViewPolicyByUserId(userId));
    }

    @Override
    public List<TypeWrapper> getMerchantViewPolicies() {
        return EMerchantViewPolicy.getAllAsObjectWrapper();
    }

    @Transactional
    @Override
    public MerchantCustomer addMerchantCustomer(Long userId, Long merchantUserId) {
        Boolean isMerchant = this.isActiveMerchant(merchantUserId);
        if(isMerchant){
            MerchantCustomer merchantCustomer=null;
            Optional<MerchantCustomer> result=merchantCustomerRepository.findByUserIdAndMerchantUserId(userId,merchantUserId);
            if(!result.isPresent()){
                merchantCustomer=new MerchantCustomer();
                merchantCustomer.setMerchant(this.getActiveMerchantInfoByUserId(merchantUserId));
                merchantCustomer.setUserId(userId);
                merchantCustomer=merchantCustomerRepository.save(merchantCustomer);
            }else
                merchantCustomer=result.get();
            return merchantCustomer;
        }
        return null;
    }

    @Override
    public ResultListPageable<MerchantCustomerWrapper> getMerchantCustomerWrappers(Map<String, Object> requestParams) {
        String sortOptions= (String)requestParams.get("sortOptions");
        if(Utils.isStringSafeEmpty(sortOptions))
            sortOptions="createDate=desc";
        List<String> sortParams =dynamicQueryHelper.getSortParams(MERCHANT_CUSTOMER_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,sortOptions);
        requestParams.put("sortParams", sortParams);
        if(!hasRoleType(ERoleType.ADMIN))
            requestParams.put("merchantUserId", Utils.getCurrentUserId());

        requestParams.put("resultSetMapping", "merchantCustomerWrapperMapping");
        ResultListPageable<MerchantCustomerWrapper> merchantResultListPageable=this.getMerchantCustomerGeneral(requestParams,false);
        return merchantResultListPageable;
    }

    private String getMerchantCustomerBaseQueryHead(Map<String, Object> requestParams ) {
        return "select mc.mcs_id as merchantCustomerId,mc.mcs_mrc_id as merchantId, m.mrc_usr_id as merchantUserId, \n" +
                "        u.usr_id as customerUserId, u.usr_username as customerUserName, u.usr_first_name as firstName,\n" +
                "        u.usr_last_name as lastName,mc.mcs_score as score, mc.mcs_create_by as createBy, \n" +
                "        mc.mcs_create_date as createDate, mc.mcs_modify_by as modifyBy, mc.mcs_modify_date  as modifyDate \n" ;
    }
    private String getMerchantCustomerBaseCountQueryHead(Map<String, Object> requestParams ) {
        return "select count(*) \n" ;
    }

    private String getMerchantCustomerBaseQueryBody(Map<String, Object> requestParams) {
        String queryString=" from sc_merchant_customer mc \n" +
                "inner join sc_user u on (u.usr_id=mc.mcs_usr_id)\n" +
                "inner join sc_merchant m on (mc.mcs_mrc_id=m.mrc_id) \n" ;
        return  queryString;
    }

    private ResultListPageable<MerchantCustomerWrapper> getMerchantCustomerGeneral(Map<String, Object> requestParams,Boolean userLimit) {
        String queryString = this.getMerchantCustomerBaseQueryHead(requestParams)+ this.getMerchantCustomerBaseQueryBody(requestParams);
        String countQueryString ="";
        Boolean resultCount = Utils.getAsBooleanFromMap(requestParams, "resultCount", false,false);
        if(resultCount)
            countQueryString=this.getMerchantCustomerBaseCountQueryHead(requestParams)+this.getMerchantCustomerBaseQueryBody(requestParams);
        return dynamicQueryHelper.getDataGeneral(requestParams,MERCHANT_CUSTOMER_REQUEST_SEARCH_MAP_NATIVE_PRIVATE_PARAMS,queryString,countQueryString,MerchantCustomerWrapper.class,userLimit);
    }
    //#endregion Merchant

}
