package com.core.topup.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ETopUpRequestStatus {
    UNKNOWN(0,"eTopUpRequestStatus.unknown"),
    SUCCESS(1,"eTopUpRequestStatus.success"),
    UN_SUCCESS(2,"eTopUpRequestStatus.unSuccess"),
    PENDING(3,"eTopUpRequestStatus.pending"),
    MANUAL_CHECK(4,"eTopUpRequestStatus.manualCheck"),
    UN_SUCCESS_RETURN_AMOUNT(5,"eTopUpRequestStatus.unSuccessReturnAmount");


    private final Integer id;
    private final String caption;

    private ETopUpRequestStatus(Integer id, String caption) {
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

    public static ETopUpRequestStatus valueOf(Integer id) {
        ETopUpRequestStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETopUpRequestStatus eTopupRequestStatus = var1[var3];
            if (eTopupRequestStatus.id.equals(id)) {
                return eTopupRequestStatus;
            }
        }
        throw new InvalidDataException("Invalid Data","eTopUpRequestStatus.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> topUpRequestStatuses = new HashMap<>();
        ETopUpRequestStatus[] eTopUpRequestStatuses = ETopUpRequestStatus.values();
        int len = eTopUpRequestStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            topUpRequestStatuses.put(eTopUpRequestStatuses[var3].id, eTopUpRequestStatuses[var3].getCaption());
        }
        return topUpRequestStatuses;
    }

    public static String captionOf(Integer  id) {
        ETopUpRequestStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETopUpRequestStatus eTopupRequestStatus = var1[var3];
            if (eTopupRequestStatus.id == id) {
                return eTopupRequestStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eTopUpRequestStatus.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ETopUpRequestStatus[] eTopUpRequestStatuses = ETopUpRequestStatus.values();
        int len = eTopUpRequestStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ETopUpRequestStatus.asObjectWrapper(eTopUpRequestStatuses[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ETopUpRequestStatus eTopupRequestStatus) {
           return new TypeWrapper( eTopupRequestStatus.getId().intValue() ,eTopupRequestStatus.getCaption() , eTopupRequestStatus.getCaption()  );
    }
}
