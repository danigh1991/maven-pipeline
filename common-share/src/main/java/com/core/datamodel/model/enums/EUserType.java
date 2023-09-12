package com.core.datamodel.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EUserType {
    REAL(0l,"eUserType.real"),
    LEGAL(1l,"eUserType.legal");

    private final Long id;
    private final String caption;

    private EUserType(Long id, String caption) {
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


    public static EUserType valueOf(Long id) {
        EUserType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUserType eUserType = var1[var3];
            if (eUserType.id .equals(id)) {
                return eUserType;
            }
        }
        throw new InvalidDataException("Invalid Data","eUserType.notFound" , id  );

    }
    public static Map<Long,String> getAllAsMap() {
        Map<Long, String> userTypes = new HashMap<>();
        EUserType[] eUserTypes = EUserType.values();
        int len = eUserTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            userTypes.put(eUserTypes[var3].id, eUserTypes[var3].getCaption());
        }
        return userTypes;
    }

    public static String captionOf(Long  id) {
        EUserType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUserType eUserType = var1[var3];
            if (eUserType.id == id) {
                return eUserType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eUserType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EUserType[] eUserTypes =EUserType.values();
        int len = eUserTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EUserType.asObjectWrapper(eUserTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EUserType eUserType) {
        return new TypeWrapper( eUserType.getId().intValue() ,eUserType.toString(), eUserType.getCaption()  );
    }


}
