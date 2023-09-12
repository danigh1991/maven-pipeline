package com.core.datamodel.model.enums;

public enum EActionSource {
    HOME_DIRECT(1),
    HOME_MAX_DISCOUNTS(2),
    HOME_NEW_DISCOUNTS(3),
    HOME_MOST_VISITED_DISCOUNTS(4),
    HOME_POPULAR_DISCOUNTS(5),
    HOME_LAST_TIME_DISCOUNTS(6),
    DETAILS_DIRECT(7),
    DETAILS_RELATED_DISCOUNTS(8),
    DETAILS_MOSTVISITED_DISCOUNTS(9),
    DETAILS_MAX_DISCOUNTS(10),
    DETAILS_SPECIAL_DISCOUNTS(11),
    DETAILS_NEW_DISCOUNTS(12),
    DETAILS_POPULAR_DISCOUNTS(13),
    STORE_BRANCH_DETAIL(14),
    BRAND_DETAIL(15),
    OTHER_EXTERNAL_SITE(16),
    STORE_DETAILS(17),
    PANEL_FOLLOW_DISCOUNTS(18),
    PANEL_LIKE_DISCOUNTS(19),
    TELEGRAM(20),
    HOME_DEFAULT_DISCOUNTS(21),
    PRODUCT_DETAILS_DIRECT(22);

    private final int value;
    private EActionSource(int value) {
        this.value = value;
    }
    public int value() {
        return this.value;
    }

    public static EActionSource valueOf(int sortCode) {
        EActionSource[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EActionSource status = var1[var3];
            if (status.value == sortCode) {
                return status;
            }
        }
        return var1[7];
     }
}
