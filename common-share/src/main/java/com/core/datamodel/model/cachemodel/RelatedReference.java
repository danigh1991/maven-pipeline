package com.core.datamodel.model.cachemodel;

import com.core.datamodel.model.enums.ETargetTypes;
import com.core.exception.InvalidDataException;

import java.io.Serializable;

public class RelatedReference implements Serializable {

    private ETargetTypes eTargetTypes;
    private Long targetId;

    public RelatedReference(ETargetTypes eTargetTypes, Long targetId) {
        this.eTargetTypes = eTargetTypes;
        this.targetId = targetId;
    }
    public RelatedReference(Integer targetTypeId, Long targetId) {
        this.eTargetTypes = ETargetTypes.valueOf(targetTypeId);
        this.targetId = targetId;
    }


    public ETargetTypes geteTargetTypes() {
        return eTargetTypes;
    }

    public void seteTargetTypes(ETargetTypes eTargetTypes) {
        this.eTargetTypes = eTargetTypes;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public  String getCacheKey(){
        if (this.eTargetTypes==null || this.targetId==null || (this.eTargetTypes!=ETargetTypes.HOME && this.targetId<=0))
            throw new InvalidDataException("Invalid Data", "global.cache.relatedRef_invalid");

        return this.eTargetTypes.name() + "_" +this.targetId;
    }
}
