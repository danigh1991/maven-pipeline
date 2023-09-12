package com.core.card.model.cardmodel.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EChannelType {
    SMS(0,"eCardChannelType.sms"),
    EMAIL(1,"eCardChannelType.email"),
    CALl(2,"eCardChannelType.call"),
    MOBILE_APP(3,"eCardChannelType.mobile_APP"),
    INTERNET_BANK(4,"eCardChannelType.internet_bank"),
    STATIC_SECRET(5,"eCardChannelType.static_secret")    ;

    private final Integer id;
    private final String caption;

    private EChannelType(int id, String caption) {
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


    public static EChannelType valueOf(int id) {
        EChannelType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EChannelType eChannelType = var1[var3];
            if (eChannelType.id == id) {
                return eChannelType;
            }
        }
        throw new InvalidDataException("Invalid Data","eCardChannelType.notFound" , id  );
    }

    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> channelTypes= new HashMap<>();
        EChannelType[] eChannelTypes = EChannelType.values();
        int len = eChannelTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            channelTypes.put(eChannelTypes[var3].id, eChannelTypes[var3].getCaption());
        }
        return channelTypes;
    }

    public static String captionOf(int  id) {
        EChannelType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EChannelType eTerminalType = var1[var3];
            if (eTerminalType.id == id) {
                return eTerminalType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCardChannelType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EChannelType[] eChannelTypes = EChannelType.values();
        int len = eChannelTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EChannelType.asObjectWrapper(eChannelTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EChannelType eChannelType) {
        return new TypeWrapper( eChannelType.getId()  ,eChannelType.toString(), eChannelType.getCaption());
    }

}
