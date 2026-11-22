package com.hackaton.crm.contact.controller;

import com.hackaton.crm.contact.service.creator.ContactCreator;
import com.hackaton.crm.contact.service.reader.ContactReader;
import com.hackaton.crm.contact.service.summary.ContactSummaryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContactController.class)
abstract class ContactControllerSlice {

    @Autowired
    protected MockMvc mockMvc;
    @MockitoBean
    protected ContactReader contactReader;
    @MockitoBean
    protected ContactSummaryReader contactSummaryReader;
    @MockitoBean
    protected ContactCreator contactCreator;
}
