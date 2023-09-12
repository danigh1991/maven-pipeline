package com.core.topup.exception;

import com.core.exception.MyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(value =HttpStatus.BAD_REQUEST)
public class TopUpException extends MyException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public TopUpException(String resourceId, String message, Object... args) {
        super(resourceId,message,args);
    }

    public TopUpException(String resourceId, List<String> errors, String message, Object... args) {
        super(resourceId,errors,message,args);
    }

}
