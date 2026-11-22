package com.hackaton.module.definition.controller;

import com.hackaton.module.definition.service.creator.ModuleDefinitionCreator;
import com.hackaton.module.definition.service.deleter.ModuleDefinitionDeleter;
import com.hackaton.module.definition.service.reader.ModuleDefinitionReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ModuleDefinitionController.class)
abstract class DefinitionControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;
    @MockitoBean
    protected ModuleDefinitionReader definitionReader;
    @MockitoBean
    protected ModuleDefinitionCreator definitionCreator;
    @MockitoBean
    protected ModuleDefinitionDeleter definitionDeleter;
}
