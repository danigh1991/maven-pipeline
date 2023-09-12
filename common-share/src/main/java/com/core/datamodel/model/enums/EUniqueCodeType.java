package com.core.datamodel.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EUniqueCodeType {
    NONE(0,"eUniqueCodeType.none"),
    NATIONAL_CODE(1,"eUniqueCodeType.national_code");


    private final int id;
    private final String caption;

    private EUniqueCodeType(int id, String caption) {
        this.id = id;
        this.caption= caption;

    }

    public int getId() {
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

    public static EUniqueCodeType valueOf(int id) {
        EUniqueCodeType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUniqueCodeType eScreenType = var1[var3];
            if (eScreenType.id == id) {
                return eScreenType;
            }
        }
        throw new InvalidDataException("Invalid Data","eScreenType.notFound" , id  );
    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> integerStringHashMap = new HashMap<>();
        EUniqueCodeType[] eUniqueCodeTypes = EUniqueCodeType.values();
        int len = eUniqueCodeTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            integerStringHashMap.put(eUniqueCodeTypes[var3].id, eUniqueCodeTypes[var3].getCaption());
        }
        return integerStringHashMap;
    }

    public static String captionOf(int  id) {
        EUniqueCodeType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUniqueCodeType eUniqueCodeType = var1[var3];
            if (eUniqueCodeType.id == id) {
                return eUniqueCodeType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eScreenType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EUniqueCodeType[] eUniqueCodeTypes = EUniqueCodeType.values();
        int len = eUniqueCodeTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EUniqueCodeType.asObjectWrapper(eUniqueCodeTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EUniqueCodeType eUniqueCodeType) {
           return new TypeWrapper( eUniqueCodeType.getId()  ,eUniqueCodeType.name(), eUniqueCodeType.getCaption()  );
    }
}
