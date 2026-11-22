package com.hackaton.crm.contact.controller;

import com.hackaton.crm.contact.dto.ContactCountsResponse;
import com.hackaton.crm.contact.dto.ContactFilter;
import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactRequest;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.ContactSummaryResponse;
import com.hackaton.crm.contact.service.creator.ContactCreator;
import com.hackaton.crm.contact.service.reader.ContactReader;
import com.hackaton.crm.contact.service.summary.ContactSummaryReader;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactReader contactReader;
    private final ContactSummaryReader contactSummaryReader;
    private final ContactCreator contactCreator;

    @GetMapping
    public List<ContactResponse> search(
            @RequestParam(defaultValue = "ALL") ContactKind kind,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean followUp,
            @RequestParam(defaultValue = "false") boolean professional) {
        return contactReader.search(new ContactFilter(kind, search, followUp, professional));
    }

    @GetMapping("/counts")
    public ContactCountsResponse count(@RequestParam(required = false) String search) {
        return contactReader.count(search);
    }

    @GetMapping("/summary")
    public ContactSummaryResponse summarize() {
        return contactSummaryReader.summarize();
    }

    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactCreator.create(request));
    }
}
