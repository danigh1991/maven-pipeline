package com.core.datamodel.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EExternalApiCallStatus {
    CREATE(0,"eExternalServiceCallStatus.create"),
    SUCCESS(1,"eExternalServiceCallStatus.success"),
    FAIL(2,"eExternalServiceCallStatus.fail");



    private final Integer id;
    private final String caption;

    private EExternalApiCallStatus(int id, String caption) {
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



    public static EExternalApiCallStatus valueOf(int id) {
        EExternalApiCallStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EExternalApiCallStatus eExternalApiCallStatus = var1[var3];
            if (eExternalApiCallStatus.id == id) {
                return eExternalApiCallStatus;
            }
        }
        throw new InvalidDataException("Invalid Data","eExternalServiceCallStatus.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> externalServiceCallStatuses= new HashMap<>();
        EExternalApiCallStatus[] eExternalApiCallStatuses = EExternalApiCallStatus.values();
        int len = eExternalApiCallStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            externalServiceCallStatuses.put(eExternalApiCallStatuses[var3].id, eExternalApiCallStatuses[var3].getCaption());
        }
        return externalServiceCallStatuses;
    }

    public static String captionOf(int  id) {
        EExternalApiCallStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EExternalApiCallStatus eExternalApiCallStatus = var1[var3];
            if (eExternalApiCallStatus.id == id) {
                return eExternalApiCallStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eExternalServiceCallStatus.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EExternalApiCallStatus[] eExternalApiCallStatuses = EExternalApiCallStatus.values();
        int len = eExternalApiCallStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EExternalApiCallStatus.asObjectWrapper(eExternalApiCallStatuses[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EExternalApiCallStatus eExternalApiCallStatus) {
        return new TypeWrapper( eExternalApiCallStatus.getId()  , eExternalApiCallStatus.getCaption(), eExternalApiCallStatus.getCaption());
    }

}
