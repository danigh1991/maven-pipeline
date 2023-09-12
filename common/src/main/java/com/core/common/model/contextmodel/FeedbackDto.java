package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class FeedbackDto implements Serializable{

    @NotNullStr(message = "{common.feedback.comment_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String comment;

}
