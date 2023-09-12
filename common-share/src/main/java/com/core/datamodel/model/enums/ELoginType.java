package com.core.datamodel.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ELoginType {
    ONLY_OTP(0l,"eLoginType.only_otp"),
    ONLY_PASSWORD(1l,"eLoginType.Only_Password"),
    OTP_PASSWORD(2l,"eLoginType.Otp_password");


    private final Long id;
    private final String caption;

    private ELoginType(Long id, String caption) {
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

    public static ELoginType valueOf(Long id) {
        ELoginType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ELoginType eLoginType = var1[var3];
            if (eLoginType.id .equals(id)) {
                return eLoginType;
            }
        }
        throw new InvalidDataException("Invalid Data","eLoginType.notFound" , id  );
    }

    public static  Map<Long,String> getAllAsMap() {
        Map<Long, String> loginTypes = new HashMap<>();
        ELoginType[] eNotifyTypes = ELoginType.values();
        int len = eNotifyTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            loginTypes.put(eNotifyTypes[var3].id, eNotifyTypes[var3].getCaption());
        }
        return loginTypes;
    }

    public static String captionOf(Long  id) {
        ELoginType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ELoginType eLoginType = var1[var3];
            if (eLoginType.id == id) {
                return eLoginType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eLoginType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ELoginType[] eLoginTypes = ELoginType.values();
        int len = eLoginTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ELoginType.asObjectWrapper(eLoginTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ELoginType eLoginType) {
           return new TypeWrapper( eLoginType.getId().intValue() ,eLoginType.toString(), eLoginType.getCaption()  );
    }
}
