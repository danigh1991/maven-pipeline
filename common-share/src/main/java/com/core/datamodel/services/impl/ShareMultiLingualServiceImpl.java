package com.core.datamodel.services.impl;

import com.core.datamodel.model.dbmodel.MultiLingualAccount;
import com.core.datamodel.model.dbmodel.MultiLingualBase;
import com.core.datamodel.model.dbmodel.MultiLingualMessageResource;
import com.core.exception.InvalidDataException;
import com.core.model.dbmodel.AbstractMultiLingualValueEntity;
import com.core.model.enums.ERepository;
import com.core.services.impl.AbstractMultiLingualServiceImpl;
import org.springframework.stereotype.Service;

@Service("shareMultiLingualServiceImpl")
public class ShareMultiLingualServiceImpl extends AbstractMultiLingualServiceImpl {

    protected AbstractMultiLingualValueEntity createNewMultiLingualEntity(ERepository iRepository){
        if (iRepository==ERepository.MultiLingualBase)
            return new MultiLingualBase();
        else if (iRepository==ERepository.MultiLingualMessageResource)
            return new MultiLingualMessageResource();
        else if (iRepository==ERepository.MultiLingualAccount)
            return new MultiLingualAccount();
        else
            throw new InvalidDataException("Invalid Data", "MultiLingual Target Table NotFound");
    }
}
