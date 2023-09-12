package com.core.accounting.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EMerchantViewPolicy {
    SELF(1,"eMerchantViewPolicy.self"),
    ALL(2,"eMerchantViewPolicy.all");


    private final Integer id;
    private final String caption;

    private EMerchantViewPolicy(Integer id, String caption) {
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

    public static EMerchantViewPolicy valueOf(Integer id) {
        EMerchantViewPolicy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EMerchantViewPolicy eCreditType = var1[var3];
            if (eCreditType.id.equals(id)) {
                return eCreditType;
            }
        }
        throw new InvalidDataException("Invalid Data","eMerchantViewPolicy.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> merchantViewPolicies = new HashMap<>();
        EMerchantViewPolicy[] eMerchantViewPolicies = EMerchantViewPolicy.values();
        int len = eMerchantViewPolicies.length;
        for (int var3 = 0; var3 < len; ++var3) {
            merchantViewPolicies.put(eMerchantViewPolicies[var3].id, eMerchantViewPolicies[var3].getCaption());
        }
        return merchantViewPolicies;
    }

    public static String captionOf(Integer  id) {
        EMerchantViewPolicy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EMerchantViewPolicy eMerchantViewPolicy = var1[var3];
            if (eMerchantViewPolicy.id == id) {
                return eMerchantViewPolicy.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eMerchantViewPolicy.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EMerchantViewPolicy[] eMerchantViewPolicies = EMerchantViewPolicy.values();
        int len = eMerchantViewPolicies.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EMerchantViewPolicy.asObjectWrapper(eMerchantViewPolicies[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EMerchantViewPolicy eMerchantViewPolicy) {
           return new TypeWrapper( eMerchantViewPolicy.getId().intValue() ,eMerchantViewPolicy.getCaption() , eMerchantViewPolicy.getCaption()  );
    }
}
