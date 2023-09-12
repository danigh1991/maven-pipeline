package com.core.common.queue.consumer;

import com.core.common.services.CommonService;
import com.core.model.enums.*;
import com.core.model.qmodel.MyQueueData;
import com.core.common.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.HashMap;

@Service("commonQueueConsumer")
public class CommonQueueConsumer implements Consumer<Event<MyQueueData>> {

    @Autowired
    private CommonService commonService;


    @Override
    public void accept(Event<MyQueueData> myQueueData) {
           HashMap<String, Object> args = (HashMap<String, Object>) myQueueData.getData().getObj();
          if (myQueueData.getData().getEMyQueueOperation()==EMyQueueOperation.SEND_CONFIRM_CODE){
              commonService.sendConfirmCode(Utils.getAsLongFromMap(args,"smsBodyCodeId",true)
                      ,Utils.getAsStringFromMap(args,"userMobileNumber",false), Utils.getAsStringFromMap(args,"userEmail",false),
                      (String[]) args.get("mobileMessage"),(String[]) args.get("emailMessage"));
          }
    }


}
