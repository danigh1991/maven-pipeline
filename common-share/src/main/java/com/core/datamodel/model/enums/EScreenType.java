package com.core.datamodel.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EScreenType {
    NONE(0,"eScreenType.none"),
    DESKTOP(1,"eScreenType.desktop"),
    MOBILE(2,"eScreenType.mobile");

    private final int id;
    private final String caption;

    private EScreenType(int id, String caption) {
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

    public static EScreenType valueOf(int id) {
        EScreenType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EScreenType eScreenType = var1[var3];
            if (eScreenType.id == id) {
                return eScreenType;
            }
        }
        throw new InvalidDataException("Invalid Data","eScreenType.notFound" , id  );
    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> integerStringHashMap = new HashMap<>();
        EScreenType[] eScreenTypes = EScreenType.values();
        int len = eScreenTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            integerStringHashMap.put(eScreenTypes[var3].id, eScreenTypes[var3].getCaption());
        }
        return integerStringHashMap;
    }

    public static String captionOf(int  id) {
        EScreenType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EScreenType eScreenType = var1[var3];
            if (eScreenType.id == id) {
                return eScreenType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eScreenType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EScreenType[] eScreenTypes = EScreenType.values();
        int len = eScreenTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EScreenType.asObjectWrapper(eScreenTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EScreenType eScreenType) {
           return new TypeWrapper( eScreenType.getId()  ,eScreenType.name(), eScreenType.getCaption()  );
    }
}
