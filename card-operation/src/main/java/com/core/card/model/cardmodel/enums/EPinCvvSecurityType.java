package com.core.card.model.cardmodel.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EPinCvvSecurityType {
    NO_SECURE(0),
    ONLY_PIN(1),
    PIN_CVV(2);


    private final Integer id;

    private EPinCvvSecurityType(int id) {
            this.id = id;
    }

    public Integer getId() {
        return this.id;
    }


    public static EPinCvvSecurityType valueOf(int id) {
        EPinCvvSecurityType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EPinCvvSecurityType eSecurityType = var1[var3];
            if (eSecurityType.id == id) {
                return eSecurityType;
            }
        }
        throw new InvalidDataException("Invalid Data","ePinCvvSecurityType.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> securityTypes= new HashMap<>();
        EPinCvvSecurityType[] ePinCvvSecurityTypes = EPinCvvSecurityType.values();
        int len = ePinCvvSecurityTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            securityTypes.put(ePinCvvSecurityTypes[var3].id, ePinCvvSecurityTypes[var3].name());
        }
        return securityTypes;
    }

    public static String captionOf(int  id) {
        EPinCvvSecurityType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EPinCvvSecurityType ePinCvvSecurityType = var1[var3];
            if (ePinCvvSecurityType.id == id) {
                return ePinCvvSecurityType.name();
            }
        }
        throw new InvalidDataException("Invalid Data","ePinCvvSecurityType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EPinCvvSecurityType[] ePinCvvSecurityTypes = EPinCvvSecurityType.values();
        int len = ePinCvvSecurityTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EPinCvvSecurityType.asObjectWrapper(ePinCvvSecurityTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EPinCvvSecurityType ePinCvvSecurityType) {
        return new TypeWrapper( ePinCvvSecurityType.getId()  ,ePinCvvSecurityType.toString(), ePinCvvSecurityType.name());
    }

}
