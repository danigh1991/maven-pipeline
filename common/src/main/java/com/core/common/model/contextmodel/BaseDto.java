package com.core.common.model.contextmodel;

import com.core.common.util.validator.BaseValidationGroup;
import com.core.model.veiw.BaseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class BaseDto implements Serializable {
    @JsonView(BaseJsonView.IdVersionView.class)
    @NotNull(message = "common.id_required", groups = BaseValidationGroup.class)
    private Long id;

    @JsonView(BaseJsonView.IdVersionView.class)
    //@NotNull(message = "common.version_required", groups = InfraValidationGroup.class)
    private Integer version;
}

