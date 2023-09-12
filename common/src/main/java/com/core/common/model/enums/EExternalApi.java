package com.core.common.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EExternalApi {
    SHAHKAR(1,"eExternalApi.shahkar"),
    GHADIR_KYC(2,"eExternalApi.ghadir_kyc"),
    CIVIL_REGISTRY_ESTELEM(3,"eExternalApi.civil_registry_estelem"),
    SHP_CARD_ENROLLMENT(10,"eExternalApi.shp_card_enrollment"),
    SHP_CARD_INFO(11,"eExternalApi.shp_card_info"),
    SHP_RENEW_CARD_ID(12,"eExternalApi.shp_renew_card_id"),
    SHP_APP_REACTIVATION(13,"eExternalApi.shp_app_reactivation"),
    SHP_CARD_HOLDER_INQUIRY(14,"eExternalApi.shp_card_holder_inquiry"),
    SHP_CARD_TRANSFER_SYNC(15,"eExternalApi.shp_card_transfer_sync"),
    SHP_CONFIRMATION_SYNC(16,"eExternalApi.shp_confirmation_sync"),
    SHP_TRANSACTION_INQUIRY(17,"eExternalApi.shp_transaction_inquiry"),
    SHP_REQUEST_OTP(18,"eExternalApi.shp_request_otp"),

    TOPUP_TOKEN(50,"eExternalApi.topup_token"),
    MCI_PRODUCT_LIST(51,"eExternalApi.mci_product_list"),
    INTERNET_PACKAGE_LIST(52,"eExternalApi.internet_package_list"),
    TOPUP_CHARGE_RESERVE(53,"eExternalApi.topup_charge_reserve"),
    TOPUP_SALE(54,"eExternalApi.topup_sale"),
    TOPUP_PACKAGE_RESERVE(55,"eExternalApi.topup_package_reserve"),
    TOPUP_SALE_STATUS(56,"eExternalApi.topup_sale_status"),
    TOPUP_RESALE(57,"eExternalApi.topup_sale"),



    GHABZINO_TOKEN(80,"eExternalApi.ghabzino_token"),
    GHABZINO_ELECTRICITY_INQUIRY(81,"eExternalApi.ghabzino_electricity_inquiry"),
    GHABZINO_GAS_INQUIRY(82,"eExternalApi.ghabzino_gas_inquiry"),
    GHABZINO_WATER_INQUIRY(83,"eExternalApi.ghabzino_water_inquiry"),
    GHABZINO_FIXED_LINE_INQUIRY(84,"eExternalApi.ghabzino_fixed_line_inquiry"),
    GHABZINO_REQUEST_PAY_BILL_FROM_WALLET(85,"eExternalApi.ghabzino_request_pay_bill_from_wallet"),
    GHABZINO_CONFIRM_PAY_BILL_FROM_WALLET(86,"eExternalApi.ghabzino_confirm_pay_bill_from_wallet"),
    GHABZINO_PAYMENT_INFO(87,"eExternalApi.ghabzino_payment_info");


    private final Integer id;
    private final String caption;

    private EExternalApi(int id, String caption) {
            this.id = id;
            this.caption= caption;
        }

    public Integer getId() {
        return this.id;
    }
    public String getCaption() {
        try {
            return ShareUtils.getMessageResource(this.caption);
        }catch (Exception e){
            e.printStackTrace();
            return this.caption;
        }

    }

    public static EExternalApi valueOf(int id) {
        EExternalApi[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EExternalApi eExternalApi = var1[var3];
            if (eExternalApi.id == id) {
                return eExternalApi;
            }
        }
        throw new InvalidDataException("Invalid Data","eExternalApi.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> externalApis= new HashMap<>();
        EExternalApi[] eExternalApis = EExternalApi.values();
        int len = eExternalApis.length;
        for (int var3 = 0; var3 < len; ++var3) {
            externalApis.put(eExternalApis[var3].id, eExternalApis[var3].getCaption());
        }
        return externalApis;
    }

    public static String captionOf(int  id) {
        EExternalApi[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EExternalApi eExternalApi = var1[var3];
            if (eExternalApi.id == id) {
                return eExternalApi.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eExternalApi.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EExternalApi[] eExternalApis = EExternalApi.values();
        int len = eExternalApis.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EExternalApi.asObjectWrapper(eExternalApis[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EExternalApi eExternalApi) {
        return new TypeWrapper( eExternalApi.getId()  ,eExternalApi.toString(), eExternalApi.getCaption());
    }

}
