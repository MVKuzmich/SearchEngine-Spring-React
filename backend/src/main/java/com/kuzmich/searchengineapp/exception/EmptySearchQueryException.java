package com.kuzmich.searchengineapp.exception;

import com.kuzmich.searchengineapp.dto.ResultDTO;

public class EmptySearchQueryException extends RuntimeException {
    public EmptySearchQueryException(String message) {
        super(message);
    }
}
