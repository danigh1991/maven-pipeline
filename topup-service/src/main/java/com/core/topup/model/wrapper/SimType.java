package com.core.topup.model.wrapper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SimType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String caption;
    private List<PackagePeriod> periods;

    public SimType(Integer id, String caption, List<PackagePeriod> periods) {
        this.id = id;
        this.caption = caption;
        this.periods = periods;
    }
}
