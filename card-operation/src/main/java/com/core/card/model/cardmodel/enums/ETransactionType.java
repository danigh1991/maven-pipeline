package com.core.card.model.cardmodel.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ETransactionType {
    TRANSFER(0,"eCardTransactionType.transfer"),
    BALANCE_INQUIRY(1,"eCardTransactionType.balance_inquiry"),
    SHORT_STATEMENT(2,"eCardTransactionType.short_statement"),
    BILL_PAYMENT(3,"eCardTransactionType.bill_payment"),
    BUY_CHARGE(4,"eCardTransactionType.buy_charge"),
    CONFIRM_CARD_SECRET(5,"eCardTransactionType.confirm_card_secret");

    private final Integer id;
    private final String caption;

    private ETransactionType(int id, String caption) {
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


    public static ETransactionType valueOf(int id) {
        ETransactionType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETransactionType eSecurityType = var1[var3];
            if (eSecurityType.id == id) {
                return eSecurityType;
            }
        }
        throw new InvalidDataException("Invalid Data","eCardTransactionType.notFound" , id  );
    }

    public static Map<Integer,String> getAllAsMap() {
        Map<Integer, String> transactionTypes= new HashMap<>();
        ETransactionType[] eTransactionTypes = ETransactionType.values();
        int len = eTransactionTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            transactionTypes.put(eTransactionTypes[var3].id, eTransactionTypes[var3].getCaption());
        }
        return transactionTypes;
    }

    public static String captionOf(int  id) {
        ETransactionType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETransactionType eTransactionType = var1[var3];
            if (eTransactionType.id == id) {
                return eTransactionType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eCardTransactionType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ETransactionType[] eTransactionTypes = ETransactionType.values();
        int len = eTransactionTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ETransactionType.asObjectWrapper(eTransactionTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ETransactionType eTransactionType) {
        return new TypeWrapper( eTransactionType.getId()  ,eTransactionType.toString(), eTransactionType.getCaption());
    }

}
