package com.hackaton.crm.timeline.service;

import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.timeline.dto.TimelineEntry;
import java.util.List;

public interface TimelineReader {

    List<TimelineEntry> findTimeline(PartyType partyType, Long partyId);
}
