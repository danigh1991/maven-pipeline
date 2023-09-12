package com.core.common.model.contextmodel;


import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EAddress extends CAddress implements Serializable {

    @NotNull(message = "{common.address.id_required}")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
