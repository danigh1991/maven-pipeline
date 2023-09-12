package com.core.common.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EUserConcurrentStrategy {
    LOGIN_DENY(0,"userConcurrentStrategy.login_deny"),
    EJECT_ALL(1,"userConcurrentStrategy.eject_all"),
    EJECT_FIRST(2,"userConcurrentStrategy.eject_first"),
    EJECT_LAST(3,"userConcurrentStrategy.eject_last");


    private final Integer id;
    private final String caption;

    private EUserConcurrentStrategy(int id, String caption) {
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


    public static EUserConcurrentStrategy valueOf(int id) {
        EUserConcurrentStrategy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUserConcurrentStrategy eUserConcurrentStrategy = var1[var3];
            if (eUserConcurrentStrategy.id == id) {
                return eUserConcurrentStrategy;
            }
        }
        throw new InvalidDataException("Invalid Data","userConcurrentStrategy.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> userConcurrentStrategy= new HashMap<>();
        EUserConcurrentStrategy[] eUserConcurrentStrategies = EUserConcurrentStrategy.values();
        int len = eUserConcurrentStrategies.length;
        for (int var3 = 0; var3 < len; ++var3) {
            userConcurrentStrategy.put(eUserConcurrentStrategies[var3].id, eUserConcurrentStrategies[var3].getCaption());
        }
        return userConcurrentStrategy;
    }

    public static String captionOf(int  id) {
        EUserConcurrentStrategy[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUserConcurrentStrategy eUserConcurrentStrategy = var1[var3];
            if (eUserConcurrentStrategy.id == id) {
                return eUserConcurrentStrategy.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","userConcurrentStrategy.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EUserConcurrentStrategy[] eUserConcurrentStrategies = EUserConcurrentStrategy.values();
        int len = eUserConcurrentStrategies.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EUserConcurrentStrategy.asObjectWrapper(eUserConcurrentStrategies[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EUserConcurrentStrategy eUserConcurrentStrategy) {
        return new TypeWrapper( eUserConcurrentStrategy.getId()  ,eUserConcurrentStrategy.toString(), eUserConcurrentStrategy.getCaption());
    }

}
