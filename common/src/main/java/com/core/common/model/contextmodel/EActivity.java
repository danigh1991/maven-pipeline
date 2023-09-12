package com.core.common.model.contextmodel;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EActivity extends CActivity {
    @NotNull(message = "{common.id_required}")
    private Long id;


}
