package com.core.datamodel.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EBank {
    SAMAN(1,"SAMAN","eBank.saman"),
    MELLAT(2,"MELLAT","eBank.mellat"),
    MELLI(3,"MELLI","eBank.melli"),
    PAYPAL(4,"PAYPAL","eBank.paypal"),
    MYPOS(19,"MYPOS","eBank.mypos"),
    STRIPE(20,"STRIPE","eBank.stripe"),
    SAMAT(21,"SAMAT","eBank.samat");



    private final Integer id;
    private final String name;
    private final String caption;
    private final String logoUrl;

    private EBank(int id, String nsme, String caption) {
            this.id = id;
            this.name=nsme;
            this.caption= caption;
            this.logoUrl= ShareUtils.getFileResourceDomain() + "/assets/img/bank/" + this.getName().toLowerCase() + ".jpg?v=1";
        }

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return name;
    }
    public String getCaption() {
        try {
            return ShareUtils.getMessageResource(this.caption);
        }catch (Exception e){
            e.printStackTrace();
            return this.caption;
        }

    }
    public String getLogoUrl()
    {
        return this.logoUrl;
    }



    public static EBank valueOf(int id) {
        EBank[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EBank eBank = var1[var3];
            if (eBank.id == id) {
                return eBank;
            }
        }
        throw new InvalidDataException("Invalid Data","eBank.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> banks= new HashMap<>();
        EBank[] eBank = EBank.values();
        int len = eBank.length;
        for (int var3 = 0; var3 < len; ++var3) {
            banks.put(eBank[var3].id, eBank[var3].getCaption());
        }
        return banks;
    }

    public static String captionOf(int  id) {
        EBank[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EBank eBank = var1[var3];
            if (eBank.id == id) {
                return eBank.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eBank.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EBank[] eBanks = EBank.values();
        int len = eBanks.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EBank.asObjectWrapper(eBanks[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EBank eBank) {
        return new TypeWrapper( eBank.getId()  ,eBank.getName(), eBank.getCaption(),eBank.getLogoUrl(),"");
    }

}
