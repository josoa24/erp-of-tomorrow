package com.hackaton.crm.interaction.controller;

import com.hackaton.crm.interaction.dto.InteractionRequest;
import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.interaction.service.creator.InteractionCreator;
import com.hackaton.crm.interaction.service.reader.InteractionReader;
import com.hackaton.crm.party.PartyType;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parties/{partyType}/{partyId}/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private static final String USER_EMAIL_HEADER = "X-User-Email";

    private final InteractionCreator interactionCreator;
    private final InteractionReader interactionReader;

    @GetMapping
    public List<InteractionResponse> findByParty(@PathVariable PartyType partyType, @PathVariable Long partyId) {
        return interactionReader.findByParty(partyType, partyId);
    }

    @PostMapping
    public ResponseEntity<InteractionResponse> create(
            @PathVariable PartyType partyType,
            @PathVariable Long partyId,
            @RequestHeader(value = USER_EMAIL_HEADER, required = false) String author,
            @Valid @RequestBody InteractionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interactionCreator.create(partyType, partyId, request, author));
    }
}
