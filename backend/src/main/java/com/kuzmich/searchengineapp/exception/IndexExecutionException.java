package com.kuzmich.searchengineapp.exception;

import com.kuzmich.searchengineapp.dto.ResultDTO;

public class IndexExecutionException extends RuntimeException {
    public IndexExecutionException(String message) {
        super(message);
    }
}
