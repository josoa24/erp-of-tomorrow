package com.hackaton.module.definition.controller;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.dto.ModuleSummary;
import com.hackaton.module.definition.service.creator.ModuleDefinitionCreator;
import com.hackaton.module.definition.service.deleter.ModuleDefinitionDeleter;
import com.hackaton.module.definition.service.reader.ModuleDefinitionReader;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/definitions")
@RequiredArgsConstructor
public class ModuleDefinitionController {

    private final ModuleDefinitionReader definitionReader;
    private final ModuleDefinitionCreator definitionCreator;
    private final ModuleDefinitionDeleter definitionDeleter;

    @GetMapping
    public List<ModuleSummary> findAll() {
        return definitionReader.findAll();
    }

    @GetMapping("/{slug}")
    public ModuleDefinitionResponse findBySlug(@PathVariable String slug) {
        return definitionReader.findBySlug(slug);
    }

    @PostMapping
    public ResponseEntity<ModuleDefinitionResponse> create(Caller caller,
            @Valid @RequestBody ModuleDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(definitionCreator.create(request, caller));
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String slug, Caller caller) {
        definitionDeleter.delete(slug, caller);
    }
}
