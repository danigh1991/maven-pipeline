package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ECreditViewType {
    SINGLE(1,"eCreditViewType.single"),
    GROUP(2,"eCreditViewType.group");

    private final Integer id;
    private final String caption;

    private ECreditViewType(Integer id, String caption) {
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

    public static ECreditViewType valueOf(Integer id) {
        ECreditViewType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ECreditViewType eCreditType = var1[var3];
            if (eCreditType.id.equals(id)) {
                return eCreditType;
            }
        }
        throw new InvalidDataException("Invalid Data","eCreditViewType.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> creditViewTypes = new HashMap<>();
        ECreditViewType[] eCreditViewTypes = ECreditViewType.values();
        int len = eCreditViewTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            creditViewTypes.put(eCreditViewTypes[var3].id, eCreditViewTypes[var3].getCaption());
        }
        return creditViewTypes;
    }

    public static String captionOf(Integer  id) {
        ECreditViewType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ECreditViewType eCreditViewType = var1[var3];
            if (eCreditViewType.id == id) {
                return eCreditViewType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCreditViewType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ECreditViewType[] eCreditViewTypes = ECreditViewType.values();
        int len = eCreditViewTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ECreditViewType.asObjectWrapper(eCreditViewTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ECreditViewType eCreditViewType) {
           return new TypeWrapper( eCreditViewType.getId().intValue() ,eCreditViewType.getCaption() , eCreditViewType.getCaption()  );
    }
}
