package com.tunaforce.hub.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public ApplicationException(HubException exception) {
        super(exception.getMessage());
        this.httpStatus = exception.getStatus();
    }

}
