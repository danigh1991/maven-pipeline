package com.core.card.model.cardmodel.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ETerminalType {
    INTERNET_PAYMENT_GATEWAY(1,"eCardTerminalType.internet_payment_gateway"),
    MOBILE_APP(5,"eCardTerminalType.mobile_app"),
    MOBILE_SDK(6,"eCardTerminalType.mobile_sdk"),
    EXPOSED_API(7,"eCardTerminalType.exposed_api");

    private final Integer id;
    private final String caption;

    private ETerminalType(int id, String caption) {
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


    public static ETerminalType valueOf(int id) {
        ETerminalType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETerminalType eTerminalType = var1[var3];
            if (eTerminalType.id == id) {
                return eTerminalType;
            }
        }
        throw new InvalidDataException("Invalid Data","eCardTerminalType.notFound" , id  );
    }

    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> terminalTypes= new HashMap<>();
        ETerminalType[] eTerminalTypes = ETerminalType.values();
        int len = eTerminalTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            terminalTypes.put(eTerminalTypes[var3].id, eTerminalTypes[var3].getCaption());
        }
        return terminalTypes;
    }

    public static String captionOf(int  id) {
        ETerminalType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETerminalType eTerminalType = var1[var3];
            if (eTerminalType.id == id) {
                return eTerminalType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCardTerminalType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ETerminalType[] eTerminalTypes = ETerminalType.values();
        int len = eTerminalTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ETerminalType.asObjectWrapper(eTerminalTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ETerminalType eTerminalType) {
        return new TypeWrapper( eTerminalType.getId()  ,eTerminalType.toString(), eTerminalType.getCaption());
    }

}
