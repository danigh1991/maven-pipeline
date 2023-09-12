package com.core.accounting.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EManualTransactionRequestStatus {
    TEMPORARY(0,"eManualTransactionRequestStatus.temporary"),
    APPROVED(1,"eManualTransactionRequestStatus.approved"),
    REJECTED(2,"eManualTransactionRequestStatus.rejected");


    private final Integer id;
    private final String caption;

    private EManualTransactionRequestStatus(Integer id, String caption) {
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

    public static EManualTransactionRequestStatus valueOf(Integer id) {
        EManualTransactionRequestStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EManualTransactionRequestStatus eManualTransactionRequestStatus = var1[var3];
            if (eManualTransactionRequestStatus.id.equals(id)) {
                return eManualTransactionRequestStatus;
            }
        }
        throw new InvalidDataException("Invalid Data","eManualTransactionRequestStatus.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> cardOperationStatuses = new HashMap<>();
        EManualTransactionRequestStatus[] eManualTransactionRequestStatuses = EManualTransactionRequestStatus.values();
        int len = eManualTransactionRequestStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            cardOperationStatuses.put(eManualTransactionRequestStatuses[var3].id, eManualTransactionRequestStatuses[var3].getCaption());
        }
        return cardOperationStatuses;
    }

    public static String captionOf(Integer  id) {
        EManualTransactionRequestStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EManualTransactionRequestStatus eManualTransactionRequestStatus = var1[var3];
            if (eManualTransactionRequestStatus.id == id) {
                return eManualTransactionRequestStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eManualTransactionRequestStatus.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EManualTransactionRequestStatus[] eManualTransactionRequestStatuses = EManualTransactionRequestStatus.values();
        int len = eManualTransactionRequestStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EManualTransactionRequestStatus.asObjectWrapper(eManualTransactionRequestStatuses[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EManualTransactionRequestStatus eManualTransactionRequestStatus) {
           return new TypeWrapper( eManualTransactionRequestStatus.getId().intValue() ,eManualTransactionRequestStatus.getCaption() , eManualTransactionRequestStatus.getCaption()  );
    }
}
