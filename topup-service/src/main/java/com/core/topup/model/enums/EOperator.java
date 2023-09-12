package com.core.topup.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EOperator {
    MCI(1,"eOperator.mci","mciAccountId"),
    MTN(2,"eOperator.mtn","mtnAccountId"),
    RAY(3,"eOperator.ray","rayAccountId");


    private final Integer id;
    private final String targetAccountConfigName;
    private final String caption;

    private EOperator(Integer id, String caption,String targetAccountConfigName) {
        this.id = id;
        this.caption= caption;
        this.targetAccountConfigName= targetAccountConfigName;
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

    public String getTargetAccountConfigName() {
        return targetAccountConfigName;
    }

    public static EOperator valueOf(Integer id) {
        EOperator[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperator eOperator = var1[var3];
            if (eOperator.id.equals(id)) {
                return eOperator;
            }
        }
        throw new InvalidDataException("Invalid Data","eOperator.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> operators = new HashMap<>();
        EOperator[] eOperators = EOperator.values();
        int len = eOperators.length;
        for (int var3 = 0; var3 < len; ++var3) {
            operators.put(eOperators[var3].id, eOperators[var3].getCaption());
        }
        return operators;
    }

    public static String captionOf(Integer  id) {
        EOperator[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperator eOperator = var1[var3];
            if (eOperator.id == id) {
                return eOperator.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eOperator.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EOperator[] eOperators = EOperator.values();
        int len = eOperators.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EOperator.asObjectWrapper(eOperators[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EOperator eOperator) {
           return new TypeWrapper( eOperator.getId().intValue() ,eOperator.name() , eOperator.getCaption()  );
    }
}
