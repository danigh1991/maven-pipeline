package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EWageType {
    NONE(0,"none","eWageType.none"),
    PERCENT(1,"ByPercent","eWageType.percent"),
    AMOUNT(2,"ByValue","eWageType.amount");


    private final int id;
    private final String name;
    private final String caption;

    private EWageType(int id, String name, String caption) {
        this.id = id;
        this.name=name;
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

    public String getName() {
        return name;
    }

    public static EWageType valueOf(int id) {
        EWageType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EWageType eWageType = var1[var3];
            if (eWageType.id == id) {
                return eWageType;
            }
        }
        throw new InvalidDataException("Invalid Data","eWageType.notFound" , id  );
    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> wageTypes = new HashMap<>();
        EWageType[] eWageTypes = EWageType.values();
        int len = eWageTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            wageTypes.put(eWageTypes[var3].id, eWageTypes[var3].getCaption());
        }
        return wageTypes;
    }

    public static String captionOf(int  id) {
        EWageType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EWageType eWageType = var1[var3];
            if (eWageType.id == id) {
                return eWageType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eWageType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EWageType[] eWageTypes = EWageType.values();
        int len = eWageTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EWageType.asObjectWrapper(eWageTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EWageType eWageType) {
           return new TypeWrapper( eWageType.getId()  ,eWageType.getName(), eWageType.getCaption()  );
    }
}
