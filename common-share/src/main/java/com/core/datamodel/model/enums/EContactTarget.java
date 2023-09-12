package com.core.datamodel.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;

import java.util.HashMap;
import java.util.Map;

public enum EContactTarget {
    SUPPORT(1),
    FINANCIAL(2),
    TECHNICAL(3),
    MANAGEMENT(4);

    private final int value;

    private EContactTarget(int value) {
        this.value = value;
    }
    public int value() {
        return this.value;
    }

    public static EContactTarget valueOf(int targetCode) {
        EContactTarget[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EContactTarget status = var1[var3];
            if (status.value == targetCode) {
                return status;
            }
        }

        throw new InvalidDataException("Invalid Data","eContactTarget.notFound" , targetCode  );
    }

    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> contactTargets = new HashMap<>();
        EContactTarget[] eContactTargets = EContactTarget.values();
        int len = eContactTargets.length;
        for (int var3 = 0; var3 < len; ++var3) {
            contactTargets.put(eContactTargets[var3].value, EContactTarget.captionOf(eContactTargets[var3].value));
        }
        return contactTargets;
    }
    public static String captionOf(int  targetCode) {
        EContactTarget[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EContactTarget status = var1[var3];
            if (status.value == targetCode) {
                switch (targetCode) {
                    case(1):
                        return ShareUtils.getMessageResource("eContactTarget.support");
                    case(2):
                        return ShareUtils.getMessageResource("eContactTarget.financial");
                    case(3):
                        return ShareUtils.getMessageResource("eContactTarget.technical");
                    case(4):
                        return ShareUtils.getMessageResource("eContactTarget.management");

                    default:
                        return ShareUtils.getMessageResource("eContactTarget.notFound" , targetCode );
                }
            }
        }
        throw new InvalidDataException("Invalid Data","eContactTarget.notFound" , targetCode  );
    }
}
