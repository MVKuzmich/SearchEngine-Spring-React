package com.kuzmich.searchengineapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzmich.searchengineapp.controller.IndexingController;
import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import com.kuzmich.searchengineapp.exception.SiteNotSaveException;
import com.kuzmich.searchengineapp.mapper.SiteMapper;
import com.kuzmich.searchengineapp.repository.SiteRepository;
import com.kuzmich.searchengineapp.service.IndexingService;
import com.kuzmich.searchengineapp.service.StatisticsService;


@WebMvcTest(controllers = {IndexingController.class})
class IndexingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private SiteRepository siteRepository;
    
    @MockBean
    private IndexingService indexingService;

    private SiteObject siteObject;

    
    @BeforeEach
    void init() {
        siteObject = SiteObject.builder()
            .name("name")
            .url("url")
       .build();
    }
  
    @Test
    void addSite_successCase() throws Exception {
           
        when(indexingService.saveSite(any(SiteObject.class))).thenReturn(new ResultDTO(true));
        
        mockMvc
            .perform(
                post("/addSite")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJsonString(siteObject)))                    
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true));
    }      


    private String toJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    
}
