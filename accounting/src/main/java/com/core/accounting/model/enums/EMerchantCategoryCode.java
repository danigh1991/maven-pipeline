package com.core.accounting.model.enums;

import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;
import com.core.util.BaseUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EMerchantCategoryCode {
    TRANSPORTATION(1l,"eMerchantTypeCode.transportation",1001),
    CHARITY(2l,"eMerchantTypeCode.charity",1100),
    PRODUCT_SELLER(3l,"eMerchantTypeCode.charity",1200),
    SERVICE_PROVIDER(4l,"eMerchantTypeCode.charity",1300),
    SERVICE_product(5l,"eMerchantTypeCode.charity",1400),
    OTHER(6l,"eMerchantTypeCode.other",2000) ;


    private final Long id;
    private final String caption;
    private final Integer code;

    private EMerchantCategoryCode(Long id, String caption, Integer code) {
        this.id = id;
        this.caption= caption;
        this.code= code;
    }

    public Long getId() {
        return this.id;
    }

    public String getCaption() {
        try {
            return BaseUtils.getMessageResource(this.caption);
        }catch (Exception e){
            e.printStackTrace();
            return this.caption;
        }

    }

    public Integer getCode() {
        return code;
    }

    public static EMerchantCategoryCode valueOf(Long id) {
        EMerchantCategoryCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EMerchantCategoryCode eMerchantCategoryCode = var1[var3];
            if (eMerchantCategoryCode.id .equals(id)) {
                return eMerchantCategoryCode;
            }
        }
        throw new InvalidDataException("Invalid Data","eMerchantTypeCode.notFound" , id  );
    }
    public static EMerchantCategoryCode valueOf(Integer code) {
        EMerchantCategoryCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EMerchantCategoryCode eMerchantCategoryCode = var1[var3];
            if (eMerchantCategoryCode.code.equals(code)) {
                return eMerchantCategoryCode;
            }
        }
        throw new InvalidDataException("Invalid Data","eMerchantTypeCode.notFound" , code );
    }


    public static  Map<Long,String> getAllAsMap() {
        Map<Long, String> merchantCategoryCodes = new HashMap<>();
        EMerchantCategoryCode[] eMerchantCategoryCodes = EMerchantCategoryCode.values();
        int len = eMerchantCategoryCodes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            merchantCategoryCodes.put(eMerchantCategoryCodes[var3].id, eMerchantCategoryCodes[var3].getCaption());
        }
        return merchantCategoryCodes;
    }

    public static String captionOf(Long  id) {
        EMerchantCategoryCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EMerchantCategoryCode eMerchantCategoryCode = var1[var3];
            if (eMerchantCategoryCode.id == id) {
                return eMerchantCategoryCode.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eMerchantTypeCode.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EMerchantCategoryCode[] eMerchantCategoryCodes = EMerchantCategoryCode.values();
        int len = eMerchantCategoryCodes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EMerchantCategoryCode.asObjectWrapper(eMerchantCategoryCodes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EMerchantCategoryCode eMerchantCategoryCode) {
           return new TypeWrapper( eMerchantCategoryCode.getId().intValue() , eMerchantCategoryCode.toString(), eMerchantCategoryCode.getCaption()  );
    }
}
