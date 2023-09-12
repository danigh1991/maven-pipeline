package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EAccountType {
    PERSONAL(1l,"eAccountType.personal",EBaseAccountType.WALLET, 2,true),
    //DEBIT(2l,"eAccountType.debit",EBaseAccountType.ACCOUNTING, 1,true) ,
    //COMMISSION(3l,"eAccountType.commission",EBaseAccountType.ACCOUNTING, 1,true) ,
    CASH_RECEIVED(2l,"eAccountType.cash_received",EBaseAccountType.ACCOUNTING, 2,false), //4
    //VAT(5l,"eAccountType.vat",EBaseAccountType.ACCOUNTING, 1,true),
    SHAREABLE(3l,"eAccountType.shareable",EBaseAccountType.WALLET, 1,true), //6
    CREDIT_PERSONAL(4l,"eAccountType.credit_personal",EBaseAccountType.WALLET, 1,true), //7
    CREDIT_ORGANIZATION(5l,"eAccountType.credit_organization",EBaseAccountType.WALLET, 1,true); //8



    private final Long id;
    private final String caption;
    private final EBaseAccountType baseAccountType;
    private final Integer nature;
    private final Boolean calcOnSettlement;

    private EAccountType(Long id, String caption,EBaseAccountType baseAccountType, Integer nature,Boolean calcOnSettlement) {
        this.id = id;
        this.caption= caption;
        this.baseAccountType= baseAccountType;
        this.nature= nature;
        this.calcOnSettlement=calcOnSettlement;
    }

    public Long getId() {
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

    public Integer getNature() {
        return nature;
    }

    public Boolean getCalcOnSettlement() {
        return calcOnSettlement;
    }

    public static EAccountType valueOf(Long id) {
        EAccountType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EAccountType eAccountType = var1[var3];
            if (eAccountType.id == id) {
                return eAccountType;
            }
        }
        throw new InvalidDataException("Invalid Data","eAccountType.notFound" , id );
    }
    public static  Map<Long,String> getAllAsMap() {
        Map<Long, String> accountTypes = new HashMap<>();
        EAccountType[] eAccountTypes = EAccountType.values();
        int len = eAccountTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            accountTypes.put(eAccountTypes[var3].id, eAccountTypes[var3].getCaption());
        }
        return accountTypes;
    }

    public static String captionOf(Long  id) {
        EAccountType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EAccountType eAccountType = var1[var3];
            if (eAccountType.id == id) {
                return eAccountType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eAccountType.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EAccountType[] eAccountTypes = EAccountType.values();
        int len = eAccountTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EAccountType.asObjectWrapper(eAccountTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EAccountType eAccountType) {
           return new TypeWrapper( eAccountType.getId().intValue() ,eAccountType.toString(), eAccountType.getCaption()  );
    }
}
