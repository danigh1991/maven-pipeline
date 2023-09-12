package com.core.common.model.contextmodel;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class EFUser extends EUser {

    @NotNull(message = "{common.user.enable_required}")
    private Boolean enable;
    private String expireDate;

    private Long loginType;
    private List<Long> roles;
    private List<String> limitIpList;
}
