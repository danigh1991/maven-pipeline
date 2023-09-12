package com.core.accounting.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EOperationRequestStatus {
    AWAITING_PAYMENT(0,"eOperationRequestStatus.awaiting_payment"),
    SUCCESS(1,"eOperationRequestStatus.success"),
    UN_SUCCESS(2,"eOperationRequestStatus.unSuccess");

    private final Integer id;
    private final String caption;

    EOperationRequestStatus(Integer id, String caption) {
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

    public static EOperationRequestStatus valueOf(Integer id) {
        EOperationRequestStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperationRequestStatus eOperationRequestStatus = var1[var3];
            if (eOperationRequestStatus.id.equals(id)) {
                return eOperationRequestStatus;
            }
        }
        throw new InvalidDataException("Invalid Data","eOperationRequestStatus.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> CardOperationStatuses = new HashMap<>();
        EOperationRequestStatus[] eOperationRequestStatuses = EOperationRequestStatus.values();
        int len = eOperationRequestStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            CardOperationStatuses.put(eOperationRequestStatuses[var3].id, eOperationRequestStatuses[var3].getCaption());
        }
        return CardOperationStatuses;
    }

    public static String captionOf(Integer  id) {
        EOperationRequestStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperationRequestStatus eOperationRequestStatus = var1[var3];
            if (eOperationRequestStatus.id == id) {
                return eOperationRequestStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eOperationRequestStatus.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EOperationRequestStatus[] eOperationRequestStatuses = EOperationRequestStatus.values();
        int len = eOperationRequestStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EOperationRequestStatus.asObjectWrapper(eOperationRequestStatuses[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EOperationRequestStatus eOperationRequestStatus) {
           return new TypeWrapper( eOperationRequestStatus.getId().intValue() ,eOperationRequestStatus.getCaption() , eOperationRequestStatus.getCaption()  );
    }
}
