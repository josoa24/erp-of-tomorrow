package com.hackaton.crm.timeline.service;

import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.RemoteOrder;
import com.hackaton.crm.party.client.RemoteSale;
import com.hackaton.crm.party.client.SupplierClient;
import com.hackaton.crm.party.service.PartyVerifier;
import com.hackaton.crm.timeline.dto.TimelineEntry;
import com.hackaton.crm.timeline.dto.TimelineKind;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineReaderService implements TimelineReader {

    private static final String SALE_VALIDATED = "VALIDATED";
    private static final Set<String> COMMITTED_ORDER = Set.of("VALIDATED", "RECEIVED");

    private final InteractionRepository interactionRepository;
    private final CustomerClient customerClient;
    private final SupplierClient supplierClient;
    private final PartyVerifier partyVerifier;

    @Override
    public List<TimelineEntry> findTimeline(PartyType partyType, Long partyId) {
        partyVerifier.verify(partyType, partyId);
        List<TimelineEntry> entries = new ArrayList<>();
        interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(partyType, partyId)
                .forEach(interaction -> entries.add(toEntry(interaction)));
        if (partyType == PartyType.CUSTOMER) {
            customerClient.findSales(partyId).stream()
                    .filter(sale -> SALE_VALIDATED.equals(sale.status()) && sale.validatedAt() != null)
                    .forEach(sale -> entries.add(toEntry(sale)));
        } else {
            supplierClient.findOrders(partyId).stream()
                    .filter(order -> COMMITTED_ORDER.contains(order.status()))
                    .forEach(order -> entries.add(toEntry(order)));
        }
        entries.sort(Comparator.comparing(TimelineEntry::date).reversed());
        return entries;
    }

    private TimelineEntry toEntry(Interaction interaction) {
        return new TimelineEntry(
                TimelineKind.INTERACTION,
                interaction.getOccurredAt(),
                interaction.getType().name(),
                interaction.getSummary(),
                null,
                interaction.getId());
    }

    private TimelineEntry toEntry(RemoteSale sale) {
        return new TimelineEntry(
                TimelineKind.SALE, sale.validatedAt(), "Vente #" + sale.id(), null, sale.totalAmount(), sale.id());
    }

    private TimelineEntry toEntry(RemoteOrder order) {
        Instant date = order.receivedAt() != null ? order.receivedAt() : order.validatedAt();
        return new TimelineEntry(
                TimelineKind.PURCHASE_ORDER,
                date,
                "Bon de commande #" + order.id(),
                order.status(),
                order.totalAmount(),
                order.id());
    }
}
