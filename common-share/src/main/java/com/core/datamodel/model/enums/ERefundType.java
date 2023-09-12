package com.core.datamodel.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ERefundType {
    USER_REQUEST(1,"USER_REQUEST","eRefundType.user_request");

    private final Integer id;
    private final String name;
    private final String caption;

    private ERefundType(Integer id, String name, String caption) {
        this.id = id;
        this.name=name;
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

    public String getName() {
        return name;
    }

    public static ERefundType valueOf(Integer id) {
        ERefundType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ERefundType eRefundType = var1[var3];
            if (eRefundType.id == id) {
                return eRefundType;
            }
        }

        throw new InvalidDataException("Invalid Data","eRefundType.notFound" , id  );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> refundTypes = new HashMap<>();
        ERefundType[] eRefundTypes = ERefundType.values();
        int len = eRefundTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            refundTypes.put(eRefundTypes[var3].id, eRefundTypes[var3].getCaption());
        }
        return refundTypes;
    }

    public static String captionOf(Integer  id) {
        ERefundType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ERefundType eRefundType = var1[var3];
            if (eRefundType.id == id) {
                return eRefundType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eRefundType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ERefundType[] eRefundTypes = ERefundType.values();
        int len = eRefundTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ERefundType.asObjectWrapper(eRefundTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ERefundType eRefundType) {
           return new TypeWrapper( eRefundType.getId().intValue() ,eRefundType.getName(), eRefundType.getCaption()  );
    }
}
