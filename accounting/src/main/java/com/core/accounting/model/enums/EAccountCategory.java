package com.core.accounting.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EAccountCategory {
    GLOBAL(1,"eAccountCategory.global"),
    INCOME(2,"eAccountCategory.income"),
    DEBIT(3,"eAccountCategory.debit"),
    CREDIT(4,"eAccountCategory.credit"),
    COST(5,"eAccountCategory.cost");

    private final Integer id;
    private final String caption;

    private EAccountCategory(Integer id, String caption) {
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

    public static EAccountCategory valueOf(Integer id) {
        EAccountCategory[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EAccountCategory eAccountCategory = var1[var3];
            if (eAccountCategory.id.equals(id)) {
                return eAccountCategory;
            }
        }
        throw new InvalidDataException("Invalid Data","eAccountCategory.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> accountCategories = new HashMap<>();
        EAccountCategory[] eAccountCategories = EAccountCategory.values();
        int len = eAccountCategories.length;
        for (int var3 = 0; var3 < len; ++var3) {
            accountCategories.put(eAccountCategories[var3].id, eAccountCategories[var3].getCaption());
        }
        return accountCategories;
    }

    public static String captionOf(Integer  id) {
        EAccountCategory[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EAccountCategory eAccountCategory = var1[var3];
            if (eAccountCategory.id == id) {
                return eAccountCategory.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eAccountCategory.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EAccountCategory[] eAccountCategories = EAccountCategory.values();
        int len = eAccountCategories.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EAccountCategory.asObjectWrapper(eAccountCategories[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EAccountCategory eAccountCategory) {
           return new TypeWrapper( eAccountCategory.getId().intValue() ,eAccountCategory.getCaption() , eAccountCategory.getCaption()  );
    }
}
