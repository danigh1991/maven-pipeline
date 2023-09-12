package com.core.datamodel.model.contextmodel;

import com.core.datamodel.model.enums.EBank;
import com.core.datamodel.model.view.MyJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralBankObject  implements Serializable{
    private Long userId;
    @JsonView(MyJsonView.GeneralBankObject.class)
    private EBank eBank;
    private String bankRequestNumber;
    @JsonView(MyJsonView.GeneralBankObject.class)
    private List<GeneralKeyValue> bankInfo = new ArrayList<>();
    @JsonView(MyJsonView.GeneralBankObject.class)
    private Long orderId=0l;
    @JsonView(MyJsonView.GeneralBankObject.class)
    private Long operationRequestId=0l;
    @JsonView(MyJsonView.GeneralBankObject.class)
    private String message;


}
