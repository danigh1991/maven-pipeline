package com.core.accounting.model.wrapper;

import com.core.accounting.model.enums.EOperation;
import com.core.accounting.model.view.AccountJsonView;
import com.core.common.util.Utils;
import com.core.datamodel.model.wrapper.AbstractMultiLingualWrapper;
import com.core.model.annotations.MultiLingual;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OperationTypeSummeryWrapper implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Double sumAmount;
}
