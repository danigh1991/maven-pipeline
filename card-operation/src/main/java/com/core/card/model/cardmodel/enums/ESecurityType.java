package com.core.card.model.cardmodel.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ESecurityType {
    NON_SECURE(0,"eCardSecurityType.non_secure"),
    DIGITAL_SIGNATURE(1,"eCardSecurityType.digital_signature"),
    MAC(2,"eCardSecurityType.mac");


    private final Integer id;
    private final String caption;

    private ESecurityType(int id, String caption) {
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


    public static ESecurityType valueOf(int id) {
        ESecurityType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ESecurityType eSecurityType = var1[var3];
            if (eSecurityType.id == id) {
                return eSecurityType;
            }
        }
        throw new InvalidDataException("Invalid Data","eCardSecurityType.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> securityTypes= new HashMap<>();
        ESecurityType[] eSecurityTypes = ESecurityType.values();
        int len = eSecurityTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            securityTypes.put(eSecurityTypes[var3].id, eSecurityTypes[var3].getCaption());
        }
        return securityTypes;
    }

    public static String captionOf(int  id) {
        ESecurityType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ESecurityType eSecurityType = var1[var3];
            if (eSecurityType.id == id) {
                return eSecurityType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCardSecurityType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ESecurityType[] eSecurityTypes = ESecurityType.values();
        int len = eSecurityTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ESecurityType.asObjectWrapper(eSecurityTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ESecurityType eSecurityType) {
        return new TypeWrapper( eSecurityType.getId()  ,eSecurityType.toString(), eSecurityType.getCaption());
    }

}
