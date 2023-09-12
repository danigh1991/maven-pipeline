package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ETransactionSourceType {
    NONE(0,"eTransactionSourceType.none"),
    WALLET(1,"eTransactionSourceType.wallet"),
    CARD(2,"eTransactionSourceType.card")  ;


    private final Integer id;
    private final String caption;

    private ETransactionSourceType(Integer id, String caption) {
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

    public static ETransactionSourceType valueOf(Integer id) {
        ETransactionSourceType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETransactionSourceType eTransactionSourceType = var1[var3];
            if (eTransactionSourceType.id == id) {
                return eTransactionSourceType;
            }
        }
        throw new InvalidDataException("Invalid Data","eTransactionSourceType.notFound" , id );
    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> transactionSourceTypes = new HashMap<>();
        ETransactionSourceType[] eTransactionSourceTypes = ETransactionSourceType.values();
        int len = eTransactionSourceTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            transactionSourceTypes.put(eTransactionSourceTypes[var3].id, eTransactionSourceTypes[var3].getCaption());
        }
        return transactionSourceTypes;
    }

    public static String captionOf(Integer id) {
        ETransactionSourceType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETransactionSourceType eTransactionSourceType = var1[var3];
            if (eTransactionSourceType.id == id) {
                return eTransactionSourceType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eTransactionSourceType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ETransactionSourceType[] eTransactionSourceTypes = ETransactionSourceType.values();
        int len = eTransactionSourceTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ETransactionSourceType.asObjectWrapper(eTransactionSourceTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ETransactionSourceType eTransactionSourceType) {
           return new TypeWrapper( eTransactionSourceType.getId().intValue() , eTransactionSourceType.toString(), eTransactionSourceType.getCaption()  );
    }
}
