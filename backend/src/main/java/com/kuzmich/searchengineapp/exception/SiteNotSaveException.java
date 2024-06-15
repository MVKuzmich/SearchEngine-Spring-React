package com.kuzmich.searchengineapp.exception;

public class SiteNotSaveException extends RuntimeException {

    public SiteNotSaveException(String message, Throwable ex) {
        super(message, ex);
    }

    public SiteNotSaveException (String message) {
        super(message);
    }
        
}
