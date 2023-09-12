package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.exception.InvalidDataException;
import com.core.util.BaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EBankPaymentStatus {
    FAIL(-2,"eBankPaymentStatus.fail"),
    REQUEST(0,"eBankPaymentStatus.request"),
    SUCCESS(1,"eBankPaymentStatus.success"),
    REVERSE(2,"eBankPaymentStatus.reverse"),
    REFUND(3,"eBankPaymentStatus.refund");


    private final Integer id;
    private final String caption;

    private EBankPaymentStatus(int id, String caption) {
            this.id = id;
            this.caption= caption;
        }

    public Integer getId() {
        return this.id;
    }
    public String getCaption() {
        try {
            return BaseUtils.getMessageResource(this.caption );
        }catch (Exception e){
            e.printStackTrace();
            return this.caption;
        }
        //return this.caption;
    }



    public static EBankPaymentStatus valueOf(int id) {
        EBankPaymentStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EBankPaymentStatus eBankPaymentStatus = var1[var3];
            if (eBankPaymentStatus.id == id) {
                return eBankPaymentStatus;
            }
        }
        throw new InvalidDataException("Invalid Data","eBankPaymentStatus.notFound" , id  );
    }
    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> bankPaymentStatuses= new HashMap<>();
        EBankPaymentStatus[] eBankPaymentStatuses = EBankPaymentStatus.values();
        int len = eBankPaymentStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            bankPaymentStatuses.put(eBankPaymentStatuses[var3].id, eBankPaymentStatuses[var3].getCaption());
        }
        return  bankPaymentStatuses;
    }

    public static String captionOf(int  id) {
        EBankPaymentStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EBankPaymentStatus eBankPaymentStatus = var1[var3];
            if (eBankPaymentStatus.id == id) {
                return eBankPaymentStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eBankPaymentStatus.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EBankPaymentStatus[] eBanks = EBankPaymentStatus.values();
        int len = eBanks.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EBankPaymentStatus.asObjectWrapper(eBanks[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EBankPaymentStatus eBank) {
        return new TypeWrapper( eBank.getId()  ,eBank.toString(), eBank.getCaption());
    }

}
