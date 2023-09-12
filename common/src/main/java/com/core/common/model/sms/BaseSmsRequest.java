package com.core.common.model.sms;

import com.core.common.util.Utils;

import java.io.Serializable;

public class BaseSmsRequest implements Serializable {

    public String getUserName() {
        return Utils.getAppPropery("app.sms.username","");
    }

    public String getPassword() {
        return Utils.getAppPropery("app.sms.password", "");
    }
}
