package com.core.common.util;

public class Convertor {


    public static Long toLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (Exception e) {
            return 0l;
        }
    }


}
