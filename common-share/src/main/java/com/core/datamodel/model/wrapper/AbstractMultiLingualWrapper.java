package com.core.datamodel.model.wrapper;

import com.core.model.dbmodel.AbstractMultiLingualEntity;
import com.core.services.MultiLingualService;
import com.core.util.BeanUtil;


public class AbstractMultiLingualWrapper extends AbstractMultiLingualEntity {
    //public  List<AbstractMultiLingualValueEntity> multiLingualTableList=null;


    public AbstractMultiLingualWrapper() {
    }


    public void prepareMultiLingual() {
        MultiLingualService multiLingualService= BeanUtil.getBean(MultiLingualService.class);
        multiLingualService.loadMultiLingualFields(this);
    }

    public void prepareMultiLingual(Boolean loadDetails) {
        MultiLingualService multiLingualService= BeanUtil.getBean(MultiLingualService.class);
        multiLingualService.loadMultiLingualFields(this,loadDetails);
    }

}
