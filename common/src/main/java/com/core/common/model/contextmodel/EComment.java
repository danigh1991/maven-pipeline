package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EComment implements Serializable{


    @NotNull(message = "{common.comment.id_required}")
    private  Long commentId;

    @NotNullStr(message = "{common.comment.comment_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String comment;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
