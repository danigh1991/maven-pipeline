package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ECreditType {
    PREPAID(1,"eCreditType.prepaid"),
    POSTPAID(2,"eCreditType.postpaid"),
    REVOLVING_POSTPAID(3,"eCreditType.revolving_postpaid");
    /*BARAT(4,"eCreditType.barat"),
    GAM(5,"eCreditType.gam");*/

    private final Integer id;
    private final String caption;

    private ECreditType(Integer id, String caption) {
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

    public static ECreditType valueOf(Integer id) {
        ECreditType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ECreditType eCreditType = var1[var3];
            if (eCreditType.id.equals(id)) {
                return eCreditType;
            }
        }
        throw new InvalidDataException("Invalid Data","eCreditType.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> creditTypes = new HashMap<>();
        ECreditType[] eCreditTypes = ECreditType.values();
        int len = eCreditTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            creditTypes.put(eCreditTypes[var3].id, eCreditTypes[var3].getCaption());
        }
        return creditTypes;
    }

    public static String captionOf(Integer  id) {
        ECreditType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ECreditType eCreditType = var1[var3];
            if (eCreditType.id == id) {
                return eCreditType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCreditType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ECreditType[] eCreditTypes = ECreditType.values();
        int len = eCreditTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ECreditType.asObjectWrapper(eCreditTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ECreditType eCreditType) {
           return new TypeWrapper( eCreditType.getId().intValue() ,eCreditType.getCaption() , eCreditType.getCaption()  );
    }
}
