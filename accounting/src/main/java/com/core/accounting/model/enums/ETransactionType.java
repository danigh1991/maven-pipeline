package com.core.accounting.model.enums;

import com.core.model.wrapper.TypeWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ETransactionType {
    PURCHASE(1,"eTransactionType.purchase" ),
    PURCHASE_CANCEL(2,"eTransactionType.purchase_cancel" ),
    PURCHASE_REJECT(3,"eTransactionType.purchase_reject" ),
    PURCHASE_NOT_DELIVERED(4,"eTransactionType.purchase_not_delivered"),
    SELLER_PAYMENT(5,"eTransactionType.seller_payment"),
    SELLER_RECEIVE(6,"eTransactionType.seller_receive" ),
    PURCHASE_RETURNED(7,"eTransactionType.purchase_returned" )  ,
    AFFILIATE_PERCENT(8,"eTransactionType.affiliate_percent" )   ,
    REJECT_EDIT_FACTOR(9,"eTransactionType.rejectEditFactor"),
    VAT(10,"eTransactionType.vat"),
    COMMISSION(11,"eTransactionType.commission" ),
    CORRECTION(12,"eTransactionType.correction" ),
    GET_MONEY_BY_REQUEST(13,"eTransactionType.get_money_by_request" ),
    MANUAL_DEPOSIT(14,"eTransactionType.manual_deposit" ),
    GIFT(15,"eTransactionType.gift"),
    BIRTHDAY_GIFT(16,"eTransactionType.birthday_gift");


    private final Integer id;
    private final String caption;

    private ETransactionType(Integer id, String caption) {
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

    public static ETransactionType valueOf(Integer id) {
        ETransactionType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETransactionType eTransactionType = var1[var3];
            if (eTransactionType.id.equals(id)) {
                return eTransactionType;
            }
        }
        throw new InvalidDataException("Invalid Data","eTransactionType.notFound" , id );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> transactionTypes = new HashMap<>();
        ETransactionType[] eTransactionTypes = ETransactionType.values();
        int len = eTransactionTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            transactionTypes.put(eTransactionTypes[var3].id, eTransactionTypes[var3].getCaption());
        }
        return transactionTypes;
    }

    public static String captionOf(Integer  id) {
        ETransactionType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETransactionType eTransactionType = var1[var3];
            if (eTransactionType.id == id) {
                return eTransactionType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eOperation.notFound" , id );
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
           return new TypeWrapper( eTransactionType.getId().intValue() ,eTransactionType.getCaption() , eTransactionType.getCaption()  );
    }
}
