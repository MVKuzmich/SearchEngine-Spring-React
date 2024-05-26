package com.kuzmich.searchengineapp;

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
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import com.kuzmich.searchengineapp.mapper.SiteMapper;
import com.kuzmich.searchengineapp.repository.SiteRepository;
import com.kuzmich.searchengineapp.service.IndexingService;
import com.kuzmich.searchengineapp.service.StatisticsService;


@WebMvcTest(controllers = {IndexingController.class})
class IndexingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SiteRepository siteRepository;

    @MockBean
    private SiteMapper siteMapper;


    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private IndexingService indexingService;

    
     /*
        I have the problem with checking which values is returned by controller
        I always got the exception java.lang.AssertionError: No value at JSON path

        The request runninig via the Rest client completes successfully
    */
    @Test
    void addSite_successCase() throws Exception {
        SiteObject siteObject = new SiteObject("url", "name");
        Site siteAfterMapper = Site.builder()
            .status(Status.NEW)
            .url("url")
            .name("name")
        .build();   
        Site siteEntity = Site.builder()
            .id(1)
            .status(Status.NEW)
            .url("url")
            .name("name")    
        .build(); 
    
        when(siteMapper.toSite(any(SiteObject.class))).thenReturn(siteAfterMapper);
        when(siteRepository.save(any(Site.class))).thenReturn(siteEntity);
        
        mockMvc
            .perform(
                post("/addSite")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJsonString(siteObject)))                    
            .andExpect(status().isOk());
    }

   
       


    private String toJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    
}
