package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EBaseAccountType {
    WALLET(1l,"eBaseAccountType.wallet"),
    ACCOUNTING(2l,"eBaseAccountType.accounting");


    private final Long id;
    private final String caption;

    private EBaseAccountType(Long id, String caption) {
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

    public static EBaseAccountType valueOf(Long id) {
        EBaseAccountType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EBaseAccountType eBaseAccountType = var1[var3];
            if (eBaseAccountType.id == id) {
                return eBaseAccountType;
            }
        }
        throw new InvalidDataException("Invalid Data","eBaseAccountType.notFound" , id );
    }
    public static  Map<Long,String> getAllAsMap() {
        Map<Long, String> baseAccountTypes = new HashMap<>();
        EBaseAccountType[] eAccountTypes = EBaseAccountType.values();
        int len = eAccountTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            baseAccountTypes.put(eAccountTypes[var3].id, eAccountTypes[var3].getCaption());
        }
        return baseAccountTypes;
    }

    public static String captionOf(Long  id) {
        EBaseAccountType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EBaseAccountType eBaseAccountType = var1[var3];
            if (eBaseAccountType.id == id) {
                return eBaseAccountType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eBaseAccountType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EBaseAccountType[] eBaseAccountTypes = EBaseAccountType.values();
        int len = eBaseAccountTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EBaseAccountType.asObjectWrapper(eBaseAccountTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EBaseAccountType eBaseAccountType) {
           return new TypeWrapper( eBaseAccountType.getId().intValue() ,eBaseAccountType.toString(), eBaseAccountType.getCaption()  );
    }
}
