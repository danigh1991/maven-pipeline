package com.core.common.model.contextmodel;


import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class QRCodeData {

    @NotNullStr(message = "common.qrcode_required")
    private String qrCodeText;

}
