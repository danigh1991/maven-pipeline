package com.core.common.queue;

import com.core.common.queue.consumer.CommonQueueConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

import static reactor.bus.selector.Selectors.$;

@Component("commonReactorStartup")
public class ReactorStartup {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private CommonQueueConsumer commonQueueConsumer;

    @PostConstruct
    public void onStartUp() {
        eventBus.on($("commonQueue"), commonQueueConsumer);
    }


}
