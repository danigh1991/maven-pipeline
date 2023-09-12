package com.core.common.model;

import com.core.common.model.enums.EConfigurationResultType;
import com.core.common.model.enums.EConfigurationType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ConfigurationModel implements Serializable {
    private EConfigurationType type;
    private String name;
    private EConfigurationResultType resultType;
    private String defaultValue;

}
