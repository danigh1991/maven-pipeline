package com.core.accounting.model.wrapper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WalletWrapper implements Serializable {

    private List<AccountWrapper> accountWrappers;
    private List<OperationTypeWrapper> globalOperation;


}
