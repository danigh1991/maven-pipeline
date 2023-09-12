package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EAccountUsageType {
    self(1l,"eAccountUsageType.self"),
    other(2l,"eAccountUsageType.other") ,
    all(3l,"eAccountUsageType.all") ;


    private final Long id;
    private final String caption;

    private EAccountUsageType(Long id, String caption) {
        this.id = id;
        this.caption= caption;
    }

    public Long getId() {
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

    public static EAccountUsageType valueOf(Long id) {
        EAccountUsageType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EAccountUsageType eAccountUsageType = var1[var3];
            if (eAccountUsageType.id == id) {
                return eAccountUsageType;
            }
        }
        throw new InvalidDataException("Invalid Data","eBaseAccountType.notFound" , id );
    }
    public static  Map<Long,String> getAllAsMap() {
        Map<Long, String> accountUsageTypes = new HashMap<>();
        EAccountUsageType[] eAccountUsageTypes = EAccountUsageType.values();
        int len = eAccountUsageTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            accountUsageTypes.put(eAccountUsageTypes[var3].id, eAccountUsageTypes[var3].getCaption());
        }
        return accountUsageTypes;
    }

    public static String captionOf(Long  id) {
        EAccountUsageType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EAccountUsageType eBaseAccountType = var1[var3];
            if (eBaseAccountType.id == id) {
                return eBaseAccountType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eBaseAccountType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EAccountUsageType[] eAccountUsageTypes = EAccountUsageType.values();
        int len = eAccountUsageTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EAccountUsageType.asObjectWrapper(eAccountUsageTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EAccountUsageType eAccountUsageType) {
           return new TypeWrapper( eAccountUsageType.getId().intValue() ,eAccountUsageType.toString(), eAccountUsageType.getCaption()  );
    }
}
