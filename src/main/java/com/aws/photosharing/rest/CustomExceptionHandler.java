/*
 * Copyright (C) 1999-2017 Concur, Inc. All Rights Reserved.
 */

package com.aws.photosharing.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    /*
     * Gets the stack trace string
     */
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(final Exception exception, final HttpServletRequest httpRequest)  {
        try {
            log.error("Exception Name: {}  Exception Message: {} Exception: {}",
                    exception.getClass().getName(), exception.getMessage(), getStackTrace(exception));
        } catch (Exception e) {
            log.error("Exception Name: {}  Exception Message: {} Exception: {}",
                    exception.getClass().getName(), exception.getMessage(), getStackTrace(e));
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}