package com.core.common.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EConfirmableOperation {
    LOGIN(1l,2,"Loging"),
    TRANSFER_MONEY(2l,2,"Transfer Money");


    private final Long id;
    private final Integer liveMinute;
    private final String caption;


    private EConfirmableOperation(Long id, Integer liveMinute,String caption) {
        this.id = id;
        this.liveMinute=liveMinute;
        this.caption=caption;
    }

    public Long getId() {
        return this.id;
    }

    public Integer getLiveMinute() {
        return liveMinute;
    }

    public String getCaption() {
        return caption;
    }

    public static EConfirmableOperation valueOf(Long id) {
        EConfirmableOperation[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EConfirmableOperation eConfirmableOperation = var1[var3];
            if (eConfirmableOperation.id .equals(id)) {
                return eConfirmableOperation;
            }
        }
        throw new InvalidDataException("Invalid Data","EConfirmableOperation.notFound" , id  );

    }
    public static Map<Long,String> getAllAsMap() {
        Map<Long, String> calendarTypes = new HashMap<>();
        EConfirmableOperation[] eConfirmableOperations = EConfirmableOperation.values();
        int len = eConfirmableOperations.length;
        for (int var3 = 0; var3 < len; ++var3) {
            calendarTypes.put(eConfirmableOperations[var3].id, eConfirmableOperations[var3].getCaption());
        }
        return calendarTypes;
    }

    public static String captionOf(Long  id) {
        EConfirmableOperation[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EConfirmableOperation eConfirmableOperation = var1[var3];
            if (eConfirmableOperation.id == id) {
                return eConfirmableOperation.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","EConfirmableOperation.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EConfirmableOperation[] eConfirmableOperations = EConfirmableOperation.values();
        int len = eConfirmableOperations.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EConfirmableOperation.asObjectWrapper(eConfirmableOperations[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EConfirmableOperation eConfirmableOperation) {
        return new TypeWrapper(eConfirmableOperation.getId().intValue() ,eConfirmableOperation.toString(), eConfirmableOperation.getCaption()  );
    }
}
