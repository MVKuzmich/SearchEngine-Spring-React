package com.kuzmich.searchengineapp.exception;

import com.kuzmich.searchengineapp.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class AppExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(IndexExecutionException.class)
    public ResponseEntity<ResultDTO> handleIndexExecutionException(IndexExecutionException ex, WebRequest webrequest) {

        return new ResponseEntity<>(new ResultDTO(false, ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(EmptySearchQueryException.class)
    public ResponseEntity<ResultDTO> handleIndexExecutionException(EmptySearchQueryException ex, WebRequest webrequest) {
        return new ResponseEntity<>(new ResultDTO(false, ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(IndexInterruptedException.class)
    public ResponseEntity<ResultDTO> handleInterruptedException(IndexInterruptedException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ResultDTO(true, ex.getMessage()), HttpStatus.OK);

    }
}
