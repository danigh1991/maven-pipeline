package com.core.datamodel.model.enums;

import com.core.exception.InvalidDataException;

public enum EMessageResourceType {
    APP(1),
    JSON(2),
    JS(3);

    private final int value;

    private EMessageResourceType(int value) {
        this.value = value;
    }
    public int value() {
        return this.value;
    }

    public static EMessageResourceType valueOf(int  eMessageResourceTypeCode) {
        EMessageResourceType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EMessageResourceType status = var1[var3];
            if (status.value == eMessageResourceTypeCode) {
                return status;
            }
        }
        throw new InvalidDataException("Invalid Data","common.id_notFound" , eMessageResourceTypeCode  );

    }
}
