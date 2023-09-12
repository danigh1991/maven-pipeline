package com.core.topup.model.view;

import com.core.accounting.model.view.AccountJsonView;


public class TopUpJsonView extends AccountJsonView {

  //#region TopUpSaleResult------------------------------------------------------------------------------
  public interface TopUpSaleResultView extends OperationRequestResultView {}
  //#endregion TopUpSaleResult

  //#region TopUpSaleResult------------------------------------------------------------------------------
  public interface TopUpRequestWrapperList extends  ResponseView{}
  public interface TopUpRequestWrapperDetail extends  TopUpRequestWrapperList{}

  //#endregion TopUpSaleResult

}
