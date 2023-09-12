package com.core.common.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EUniqueCodeValidationType {
    NONE(0,"uniqueCodeValidationService.none"),
    SHAHKAR(1,"uniqueCodeValidationService.shahkar");



    private final Integer id;
    private final String caption;

    private EUniqueCodeValidationType(int id, String caption) {
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


    public static EUniqueCodeValidationType valueOf(int id) {
        EUniqueCodeValidationType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUniqueCodeValidationType eUniqueCodeValidationType = var1[var3];
            if (eUniqueCodeValidationType.id == id) {
                return eUniqueCodeValidationType;
            }
        }
        throw new InvalidDataException("Invalid Data","uniqueCodeValidationService.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> uniqueCodeValidationType= new HashMap<>();
        EUniqueCodeValidationType[] eUniqueCodeValidationTypes = EUniqueCodeValidationType.values();
        int len = eUniqueCodeValidationTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            uniqueCodeValidationType.put(eUniqueCodeValidationTypes[var3].id, eUniqueCodeValidationTypes[var3].getCaption());
        }
        return uniqueCodeValidationType;
    }

    public static String captionOf(int  id) {
        EUniqueCodeValidationType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EUniqueCodeValidationType eBank = var1[var3];
            if (eBank.id == id) {
                return eBank.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","uniqueCodeValidationService.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EUniqueCodeValidationType[] eUniqueCodeValidationTypes = EUniqueCodeValidationType.values();
        int len = eUniqueCodeValidationTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EUniqueCodeValidationType.asObjectWrapper(eUniqueCodeValidationTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EUniqueCodeValidationType eUniqueCodeValidationType) {
        return new TypeWrapper( eUniqueCodeValidationType.getId()  ,eUniqueCodeValidationType.toString(), eUniqueCodeValidationType.getCaption());
    }

}
