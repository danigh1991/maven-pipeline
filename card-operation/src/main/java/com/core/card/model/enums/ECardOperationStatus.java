package com.core.card.model.enums;

import com.core.common.util.Utils;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ECardOperationStatus {
    CARD_HOLDER_INQUIRY(0,"eCardOperationStatus.cardHolderInquiry"),
    OTP_REQUEST(1,"eCardOperationStatus.otpRequest"),
    SUCCESS(2,"eCardOperationStatus.success"),
    UN_SUCCESS(3,"eCardOperationStatus.unSuccess");

    private final Integer id;
    private final String caption;

    private ECardOperationStatus(Integer id, String caption) {
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

    public static ECardOperationStatus valueOf(Integer id) {
        ECardOperationStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ECardOperationStatus eCardOperationStatus = var1[var3];
            if (eCardOperationStatus.id.equals(id)) {
                return eCardOperationStatus;
            }
        }
        throw new InvalidDataException("Invalid Data","eCardOperationStatus.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> cardOperationStatuses = new HashMap<>();
        ECardOperationStatus[] eCardOperationStatuses = ECardOperationStatus.values();
        int len = eCardOperationStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            cardOperationStatuses.put(eCardOperationStatuses[var3].id, eCardOperationStatuses[var3].getCaption());
        }
        return cardOperationStatuses;
    }

    public static String captionOf(Integer  id) {
        ECardOperationStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ECardOperationStatus eCardOperationStatus = var1[var3];
            if (eCardOperationStatus.id == id) {
                return eCardOperationStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCardOperationStatus.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ECardOperationStatus[] eCardOperationStatuses = ECardOperationStatus.values();
        int len = eCardOperationStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ECardOperationStatus.asObjectWrapper(eCardOperationStatuses[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ECardOperationStatus eCardOperationStatus) {
           return new TypeWrapper( eCardOperationStatus.getId().intValue() ,eCardOperationStatus.getCaption() , eCardOperationStatus.getCaption()  );
    }
}
