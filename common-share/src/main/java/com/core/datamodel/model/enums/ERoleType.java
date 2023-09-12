package com.core.datamodel.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ERoleType {
    ADMIN(1,"eRoleType.admin"),
    USER(2,"eRoleType.user");


    private final Integer id;
    private final String caption;

    private ERoleType(Integer id, String caption) {
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


    public static ERoleType valueOf(Integer id) {
        ERoleType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ERoleType eNotifyType = var1[var3];
            if (eNotifyType.id .equals(id)) {
                return eNotifyType;
            }
        }
        throw new InvalidDataException("Invalid Data","eNotifyType.notFound" , id  );

    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> notifyTypes = new HashMap<>();
        ERoleType[] eNotifyTypes = ERoleType.values();
        int len = eNotifyTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            notifyTypes.put(eNotifyTypes[var3].id, eNotifyTypes[var3].getCaption());
        }
        return notifyTypes;
    }

    public static String captionOf(Integer  id) {
        ERoleType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ERoleType eNotifyType = var1[var3];
            if (eNotifyType.id == id) {
                return eNotifyType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eNotifyType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ERoleType[] eNotifyTypes = ERoleType.values();
        int len = eNotifyTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ERoleType.asObjectWrapper(eNotifyTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ERoleType eNotifyType) {
           return new TypeWrapper( eNotifyType.getId().intValue() ,eNotifyType.toString(), eNotifyType.getCaption()  );
    }
}
