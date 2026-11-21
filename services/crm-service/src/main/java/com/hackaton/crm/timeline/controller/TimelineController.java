package com.hackaton.crm.timeline.controller;

import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.timeline.dto.TimelineEntry;
import com.hackaton.crm.timeline.service.TimelineReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parties/{partyType}/{partyId}/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineReader timelineReader;

    @GetMapping
    public List<TimelineEntry> findTimeline(@PathVariable PartyType partyType, @PathVariable Long partyId) {
        return timelineReader.findTimeline(partyType, partyId);
    }
}
