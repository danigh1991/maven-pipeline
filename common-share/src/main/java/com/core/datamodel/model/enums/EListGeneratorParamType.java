package com.core.datamodel.model.enums;

import com.core.datamodel.util.ShareUtils;
import com.core.exception.InvalidDataException;
import com.core.model.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EListGeneratorParamType {
    PRODUCT_CAROUSEL(1,"PRODUCT_CAROUSEL","eListGeneratorParamType.product_carousel"),
    LINKED_BOX_LIST(2,"LINKED_BOX_LIST","eListGeneratorParamType.linked_box_list"),
    TEXT_BOX(0,"TRXT_BOX","eListGeneratorParamType.text_box"),
    ROW_BOX(-1,"ROW_BOX","eListGeneratorParamType.row_box"),
    MENU_BOX(3,"MENU_BOX","eListGeneratorParamType.menu_box"),
    USER_BOX(4,"USER_BOX","eListGeneratorParamType.user_box"),
    BASKET_BOX(5,"BASKET_BOX","eListGeneratorParamType.basket_box"),
    PRODUCT_SEARCH_BOX(6,"PRODUCT_SEARCH_BOX","eListGeneratorParamType.product_search_box"),
    CONTACT_US(7,"CONTACT_US","eListGeneratorParamType.contact_us"),
    SIDE_MENU(8,"SIDE_MENU","eListGeneratorParamType.side_menu"),
    ACCORDION_SEARCH(9,"ACCORDION_SEARCH","eListGeneratorParamType.accordion_search"),
    DROPDOWN_USER(10,"DROPDOWN_USER","eListGeneratorParamType.dropdown_user"),
    SLIDER(11,"SLIDER","eListGeneratorParamType.slider"),
    HTML_BUILDER(12,"HTML_BUILDER","eListGeneratorParamType.html_builder"),
    DISCOUNT_ITEM_CAROUSEL(13,"DISCOUNT_ITEM_CAROUSEL","eListGeneratorParamType.discount_item_carousel"),
    LANGUAGE_LIST(14,"LANGUAGE_LIST","eListGeneratorParamType.language_list"),
    USER_ADDRESS_SELECT(15,"USER_ADDRESS_SELECT","eListGeneratorParamType.user_address_select"),
    STORE_CAROUSEL(16,"STORE_CAROUSEL", "eListGeneratorParamType.store_carousel"),
    PRESENTOR_CONTRACT(17,"PRESENTOR_CONTRACT", "eListGeneratorParamType.presentor_contract_list"),
    ARTICLE_CAROUSEL(18,"ARTICLE", "eListGeneratorParamType.article_carousel"),
    ARTICLE_LIST(19,"ARTICLE_LIST", "eListGeneratorParamType.article_list"),
    PRODUCT_LIST(20,"PRODUCT_LIST", "eListGeneratorParamType.product_list"),
    DISCOUNT_ITEM_LIST(21,"discount_item_list", "eListGeneratorParamType.discount_item_list"),
    STORE_LIST(22,"STORE_LIST", "eListGeneratorParamType.store_list");

    private final int id;
    private final String name;
    private final String caption;

    private EListGeneratorParamType(int id, String name, String caption) {
        this.id = id;
        this.name=name;
        this.caption= caption;
    }

    public int getId() {
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

    public static EListGeneratorParamType valueOf(int id) {
        EListGeneratorParamType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EListGeneratorParamType eListGeneratorParamType = var1[var3];
            if (eListGeneratorParamType.id == id) {
                return eListGeneratorParamType;
            }
        }
        throw new InvalidDataException("Invalid Data","eListGeneratorParamType.notFound" , id  );

    }
    public static  Map<Integer,String> getAllAsMap() {
        Map<Integer, String> integerStringHashMap = new HashMap<>();
        EListGeneratorParamType[] eListGeneratorParamTypes = EListGeneratorParamType.values();
        int len = eListGeneratorParamTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            integerStringHashMap.put(eListGeneratorParamTypes[var3].id, eListGeneratorParamTypes[var3].getCaption());
        }
        return integerStringHashMap;
    }

    public static String captionOf(int  id) {
        EListGeneratorParamType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EListGeneratorParamType eListGeneratorParamType = var1[var3];
            if (eListGeneratorParamType.id == id) {
                return eListGeneratorParamType.getCaption();
            }
        }
        throw new InvalidDataException("Invalid Data","eListGeneratorParamType.notFound" , id  );
    }

    public static List<TypeWrapper> getAllAsObjectWrapper() {
        List<TypeWrapper> typeWrappers=new ArrayList<>();
        String result="[";
        EListGeneratorParamType[] eListGeneratorParamTypes = EListGeneratorParamType.values();
        int len = eListGeneratorParamTypes.length;
        for (int var3 = 0; var3 < len; ++var3) {
            typeWrappers.add(EListGeneratorParamType.asObjectWrapper(eListGeneratorParamTypes[var3]));
        }
        return typeWrappers;
    }

    public static TypeWrapper asObjectWrapper(EListGeneratorParamType eListGeneratorParamType) {
           return new TypeWrapper( eListGeneratorParamType.getId()  ,eListGeneratorParamType.getName(), eListGeneratorParamType.getCaption()  );
    }
}
