package com.kuzmich.searchengineapp.exception;

public class SiteNotFoundException extends RuntimeException {

    public SiteNotFoundException (String message) {
        super(message);
    }
    
}
