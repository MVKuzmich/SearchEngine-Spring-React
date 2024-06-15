package com.kuzmich.searchengineapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzmich.searchengineapp.BaseTestIntegrationClass;
import com.kuzmich.searchengineapp.dto.SiteObject;

import lombok.RequiredArgsConstructor;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class IndexingControllerIT extends BaseTestIntegrationClass {

    private final MockMvc mockMvc;

    @Test
    public void saveSite() throws Exception {
        SiteObject body = SiteObject.builder()
                .name("name")
                .url("url")
                .build();

        mockMvc.perform(post("/add-site")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @Sql("classpath:sql/add-site.sql")
    public void saveSite_throwException_ifUrlNotUnique() throws Exception {
        SiteObject body = SiteObject.builder()
                .name("name")
                .url("url.com")
                .build();

        mockMvc.perform(post("/add-site")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(body)))
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value("Such an url has already existed"));

    }

}
