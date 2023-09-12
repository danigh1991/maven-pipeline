package com.core.topup.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ETopUpType {
    CHARGE(1),
    INTERNET_PACKAGE(2);


    private final Integer id;

    private ETopUpType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }


    public static ETopUpType valueOf(Integer id) {
        ETopUpType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETopUpType eTopUpType = var1[var3];
            if (eTopUpType.id.equals(id)) {
                return eTopUpType;
            }
        }
        throw new InvalidDataException("Invalid Data","eTopUpType.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> operators = new HashMap<>();
        ETopUpType[] topUpTypes = ETopUpType.values();
        int len = topUpTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            operators.put(topUpTypes[var3].id, topUpTypes[var3].name());
        }
        return operators;
    }

    public static String captionOf(Integer  id) {
        ETopUpType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETopUpType eTopUpType = var1[var3];
            if (eTopUpType.id == id) {
                return eTopUpType.name();
            }
        }
        throw new InvalidDataException("Invalid Data","eTopUpType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ETopUpType[] eTopUpTypes = ETopUpType.values();
        int len = eTopUpTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ETopUpType.asObjectWrapper(eTopUpTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ETopUpType eTopUpType) {
           return new TypeWrapper( eTopUpType.getId().intValue() ,eTopUpType.name() , eTopUpType.name() );
    }
}
