package com.core.accounting.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EOperation {
    CHARGE(1,"CHARGE","eOperation.charge", 1010),
    GET_MONEY(2,"GET_MONEY","eOperation.get_money" ,1011),
    MANUAL_DEPOSIT(11,"MANUAL_DEPOSIT","eOperation.manual_deposit" ,1012),
    PURCHASE(3,"PURCHASE","eOperation.purchase" , 1020),
    TRANSFER(4,"TRANSFER","eOperation.transfer" ,1030),
    GROUP_TRANSFER(5,"GROUP_TRANSFER","eOperation.group_transfer" ,1031),
    DEPOSIT_REQUEST(6,"DEPOSIT_REQUEST","eOperation.deposit_request" ,1040),
    STATEMENT(7,"STATEMENT","eOperation.statement" ,1200),
    COST_SHARE_REQUEST(8,"COST_SHARE_REQUEST","eOperation.costShareRequest" ,1050),
    MOBILE_CHARGE(9,"MOBILE_CHARGE","eOperation.mobile_charge" ,1060),
    INTERNET_CHARGE(10,"INTERNET_CHARGE","eOperation.internet_charge" ,1070),
    GIFT_TRANSFER(12,"TRANSFER","eOperation.gift_transfer" ,1032),
    CHARITY_TRANSFER(13,"CHARITY_TRANSFER","eOperation.charity_transfer" ,1033),
    TRANSPORTATION_TRANSFER(14,"TRANSPORTATION_TRANSFER","eOperation.transportation_transfer" ,1034),
    CARD_TRANSFER(20,"CARD_TRANSFER","eOperation.card_transfer" ,2010),
    BILL_PAYMENT(40,"BILL_PAYMENT","eOperation.bill_payment" ,4000);

    //PURCHASE_CANCEL(5,"PURCHASE_CANCEL","eOperation.purchase_cancel" , 1040),
    //PURCHASE_REJECT(6,"PURCHASE_REJECT","eOperation.purchase_reject" , 1041),
    //PURCHASE_NOT_DELIVERED(7,"PURCHASE_REJECT","eOperation.purchase_not_delivered" , 1042),
    //SELLER_PAYMENT(8,"SELLER_PAYMENT","eOperation.seller_payment" , 1050),
    //SELLER_RECEIVE(9,"SELLER_RECEIVE","eOperation.seller_receive" , 1051),
    //PURCHASE_RETURNED(10,"PURCHASE_RETURNED","eOperation.purchase_returned" , 1060)  ,
    //AFFILIATE_PERCENT(11,"AFFILIATE_PERCENT","eOperation.affiliate_percent" , 1070)   ,
    //REJECT_EDIT_FACTOR(12,"REJECT_EDIT_FACTOR","eOperation.rejectEditFactor" , 1080),
    //VAT(13,"VAT","eOperation.vat" , 1090),
    //COMMISSION(14,"COMMISSION","eOperation.commission" , 1100),
    //DISCODE_CHARGE(15,"DISCODE_CHARGE","eOperation.disCode_charge" , 1110),
    //RETURN_DISCODE_CHARGE(16,"RETURN_DISCODE_CHARGE","eOperation.return_disCode_charge" , 1120),
    //CORRECTION(17,"CORRECTION","eOperation.correction" , 1130);


    private final Integer id;
    private final String name;
    private final String caption;
    private final Integer operationCode;

    private EOperation(Integer id, String name, String caption,Integer operationCode) {
        this.id = id;
        this.name=name;
        this.caption= caption;
        this.operationCode= operationCode;
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

    public String getName() {
        return name;
    }

    public Integer getOperationCode() {
        return operationCode;
    }

    public static EOperation valueOf(Integer id) {
        EOperation[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperation eOperation = var1[var3];
            if (eOperation.id.equals(id)) {
                return eOperation;
            }
        }
        throw new InvalidDataException("Invalid Data","eOperation.notFound" , id );
    }

    public static EOperation valueOfCode(Integer code) {
        EOperation[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperation eOperation = var1[var3];
            if (eOperation.operationCode.equals(code)) {
                return eOperation;
            }
        }
        throw new InvalidDataException("Invalid Data","eOperation.notFound" , code );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> operations = new HashMap<>();
        EOperation[] eOperations = EOperation.values();
        int len = eOperations.length;
        for (int var3 = 0; var3 < len; ++var3) {
            operations.put(eOperations[var3].id, eOperations[var3].getCaption());
        }
        return operations;
    }

    public static String captionOf(Integer  id) {
        EOperation[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EOperation eOperation = var1[var3];
            if (eOperation.id == id) {
                return eOperation.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eOperation.notFound" , id );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EOperation[] eOperations = EOperation.values();
        int len = eOperations.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EOperation.asObjectWrapper(eOperations[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EOperation eOperation) {
           return new TypeWrapper( eOperation.getId().intValue() ,eOperation.getName(), eOperation.getCaption()  );
    }
}
