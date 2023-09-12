package com.core.datamodel.model.enums;

import com.core.exception.InvalidDataException;
import com.core.util.BaseUtils;

public enum ENotifyTargetType {
    MESSAGE_BOX(1, "eNotifyTargetType.message_box"),
    DEPOSIT_REQUEST(2, "eNotifyTargetType.deposit_request"),
    COST_SHARE_REQUEST(3, "eNotifyTargetType.cost_share_request");



    private final int value;
    private final String caption;

    private ENotifyTargetType(int value, String caption)
    {
        this.value = value;
        this.caption = caption;
    }
    public int value() {
        return this.value;
    }

    public String getCaption() {
        try {
            return BaseUtils.getMessageResource(this.caption);
        }catch (Exception e){
            e.printStackTrace();
            return this.caption;
        }

    }

    public static ENotifyTargetType valueOf(int value) {
        ENotifyTargetType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ENotifyTargetType status = var1[var3];
            if (status.value == value) {
                return status;
            }
        }

        throw new InvalidDataException("Invalid Data","eNotifyTargetType.notFound" , value);
    }

    public static String captionOf(int  value) {
        ENotifyTargetType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ENotifyTargetType eTargetType = var1[var3];
            if (eTargetType.value == value) {
                return eTargetType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eNotifyTargetType.notFound" , value );
    }
}
