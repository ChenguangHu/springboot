package com.geb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ApplicationException extends Exception {

    private static final long serialVersionUID = 20110716L;
    private String message;
    private String[] args;
    private int status;
    private String messageDetails;
    private Logger log = LoggerFactory.getLogger(getClass());
    
    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
        this.message = message;
        log.info(message);
    }

    public ApplicationException(String message, int status) {
        super(message);
        this.message = message;
        this.status = status;
        log.info(message);
    }

    public ApplicationException(String message, int status, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.status = status;
        log.info(message);
    }

    public ApplicationException(String message, String[] args) {
        super(message);
        this.args = args;
        this.message = message;
        log.info(message);
    }

    public ApplicationException(String message, String messageDetails) {
        super(message);
        this.message = message;
        this.messageDetails = messageDetails;
        log.info(message);
    }

    public String getMessage() {
        return message;
    }

    public String getMessageDetails() {
        return messageDetails;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessageDetails(String messageDetails) {
        this.messageDetails = messageDetails;
    }

    @Override
    public String getLocalizedMessage() {
        return message;
    }
}
