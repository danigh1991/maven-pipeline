package com.core.datamodel.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ERefundStatus {
    NEW(0,"NEW","eRefundStatus.new"),
    WAIT_FOR_DEPOSIT(1,"WAIT_FOR_DEPOSIT","eRefundStatus.wait_for_deposit"),
    DEPOSIT(2,"DEPOSIT","eRefundStatus.deposit"),
    REJECT(3,"REJECT","eRefundStatus.reject"),
    USER_CANCEL(4,"USER_CANCEL","eRefundStatus.user_cancel");


    private final Integer id;
    private final String name;
    private final String caption;

    private ERefundStatus(Integer id, String name, String caption) {
        this.id = id;
        this.name=name;
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

    public String getName() {
        return name;
    }

    public static ERefundStatus valueOf(Integer id) {
        ERefundStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ERefundStatus eRefundStatus = var1[var3];
            if (eRefundStatus.id == id) {
                return eRefundStatus;
            }
        }

        throw new InvalidDataException("Invalid Data","eRefundStatus.notFound" , id  );

    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> refundStatuses = new HashMap<>();
        ERefundStatus[] eRefundStatuses = ERefundStatus.values();
        int len = eRefundStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            refundStatuses.put(eRefundStatuses[var3].id, eRefundStatuses[var3].getCaption());
        }
        return refundStatuses;
    }

    public static String captionOf(Integer  id) {
        ERefundStatus[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ERefundStatus eRefundStatus = var1[var3];
            if (eRefundStatus.id == id) {
                return eRefundStatus.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eRefundStatus.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        ERefundStatus[] eRefundStatuses = ERefundStatus.values();
        int len = eRefundStatuses.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(ERefundStatus.asObjectWrapper(eRefundStatuses[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(ERefundStatus eRefundStatus) {
           return new TypeWrapper( eRefundStatus.getId().intValue() ,eRefundStatus.getName(), eRefundStatus.getCaption()  );
    }
}
