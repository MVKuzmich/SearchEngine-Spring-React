package com.kuzmich.searchengineapp.controller;

import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.exception.EmptySearchQueryException;
import com.kuzmich.searchengineapp.exception.IndexExecutionException;
import com.kuzmich.searchengineapp.exception.IndexInterruptedException;
import com.kuzmich.searchengineapp.exception.SiteNotFoundException;
import com.kuzmich.searchengineapp.exception.SiteNotSaveException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Slf4j
public class AppExceptionHandler {


    @ExceptionHandler({IndexExecutionException.class})
    @ResponseBody
    public ResultDTO handleIndexExecutionException(IndexExecutionException ex) {
        return new ResultDTO(false, ex.getMessage());
    }

    @ExceptionHandler(EmptySearchQueryException.class)
    @ResponseBody
    public ResultDTO handleIndexExecutionException(EmptySearchQueryException ex) {
        return new ResultDTO(false, ex.getMessage());
    }

    @ExceptionHandler(IndexInterruptedException.class)
    @ResponseBody
    public ResultDTO handleInterruptedException(IndexInterruptedException ex) {
        return new ResultDTO(true, ex.getMessage());

    }

    @ExceptionHandler({SiteNotSaveException.class})
    @ResponseBody
    public ResultDTO handleSiteNotSaveException(SiteNotSaveException ex) {
        
        return new ResultDTO(false, ex.getMessage()); 
    }

    @ExceptionHandler({SiteNotFoundException.class})
    @ResponseBody
    public ResultDTO handleSiteNotFoundException(SiteNotFoundException ex) {
        
        return new ResultDTO(false, ex.getMessage()); 
    }

}
