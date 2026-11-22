package com.hackaton.module.adaptation.controller;

import com.hackaton.module.adaptation.dto.AdaptationRequest;
import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.adaptation.service.creator.AdaptationCreator;
import com.hackaton.module.adaptation.service.deleter.AdaptationDeleter;
import com.hackaton.module.adaptation.service.reader.AdaptationReader;
import com.hackaton.module.common.security.Caller;
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
@RequestMapping("/adaptations")
@RequiredArgsConstructor
public class AdaptationController {

    private final AdaptationReader adaptationReader;
    private final AdaptationCreator adaptationCreator;
    private final AdaptationDeleter adaptationDeleter;

    @GetMapping
    public List<AdaptationResponse> findAll() {
        return adaptationReader.findAll();
    }

    @PostMapping
    public ResponseEntity<AdaptationResponse> activate(Caller caller, @Valid @RequestBody AdaptationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adaptationCreator.activate(request, caller));
    }

    @DeleteMapping("/{adaptationKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String adaptationKey, Caller caller) {
        adaptationDeleter.deactivate(adaptationKey, caller);
    }
}
