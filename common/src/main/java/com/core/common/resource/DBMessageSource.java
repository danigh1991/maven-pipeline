package com.core.common.resource;

import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.MessageResource;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;


public class DBMessageSource extends AbstractMessageSource {


    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
        MessageResource messageResource = Utils.getMessageInfo(key);
        if (messageResource == null) {
            messageResource = new MessageResource();
            messageResource.setContent(key);
        }
        return new MessageFormat(Utils.prepareStringMessage(messageResource.getContent()), locale);
    }

}