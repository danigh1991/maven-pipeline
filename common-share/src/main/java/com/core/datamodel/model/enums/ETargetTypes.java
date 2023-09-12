package com.core.datamodel.model.enums;

import com.core.exception.InvalidDataException;
import com.core.util.BaseUtils;

public enum ETargetTypes {
    CONTACT_US(6, "eTargetTypes.contact_us"),
    ORDER(11,"eTargetTypes.order"),
    HOME(14,"eTargetTypes.home"),
    SITE(21,"eTargetTypes.site"),
    SITE_THEME(22,"eTargetTypes.site_theme"),
    MULTI_LINGUAL_DATA(26,"eTargetTypes.multi_lingual_data"),
    COUNTRY(27,"eTargetTypes.country"),
    USER(31, "eTargetTypes.user"),
    OPERATION_REQUEST(32, "eTargetTypes.operation_request"),
    DEPOSIT_REQUEST(33, "eTargetTypes.deposit_request");



    private final int value;
    private final String caption;

    private ETargetTypes(int value, String caption)
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

    public static ETargetTypes valueOf(int targetTypeCode) {
        ETargetTypes[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETargetTypes status = var1[var3];
            if (status.value == targetTypeCode) {
                return status;
            }
        }

        throw new InvalidDataException("Invalid Data","eTargetTypes.notFound" , targetTypeCode );
    }

    public static String captionOf(int  value) {
        ETargetTypes[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ETargetTypes eTargetType = var1[var3];
            if (eTargetType.value == value) {
                return eTargetType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eTargetTypes.notFound" , value );
    }
}
