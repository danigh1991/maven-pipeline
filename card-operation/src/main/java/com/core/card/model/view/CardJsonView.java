package com.core.card.model.view;

import com.core.model.veiw.BaseJsonView;

public class CardJsonView extends BaseJsonView{

  //////////////////////BankCard ///////////////////////////
    public interface BankCardList extends ResponseView{}
    public interface BankCardDetails extends BankCardList {}

  //////////////////////CardEnrollmentResponse ///////////////////////////
    public interface CardEnrollmentResponseView extends ResponseView{}

  //////////////////////AppReactivationResponse ///////////////////////////
    public interface AppReactivationResponseView extends ResponseView{}

  //////////////////////CardHolderInquiryResponse ///////////////////////////
    public interface CardHolderInquiryResponseView extends ResponseView{}

  //////////////////////OtpDeliveryResponse ///////////////////////////
    public interface OtpDeliveryResponseView extends ResponseView{}

    //////////////////////CardTransferResponse ///////////////////////////
    public interface CardTransferResponseView extends ResponseView{}

  //////////////////////BankCardOperationWrapper ///////////////////////////
    public interface BankCardOperationWrapperList extends ResponseView{}
    public interface BankCardOperationWrapperListAdmin extends BankCardOperationWrapperList{}
    public interface BankCardOperationWrapperDetail extends BankCardOperationWrapperList{}
    public interface BankCardOperationWrapperDetailAdmin extends BankCardOperationWrapperDetail{}


}
