package com.hackaton.crm.contact.service.summary;

import static com.hackaton.crm.contact.service.ContactTally.CUSTOMER;
import static com.hackaton.crm.contact.service.ContactTally.FOLLOW_UP;
import static com.hackaton.crm.contact.service.ContactTally.SUPPLIER;
import static com.hackaton.crm.contact.service.ContactTally.countMatching;

import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.ContactSummaryResponse;
import com.hackaton.crm.contact.service.source.ContactSource;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.RemoteCustomerSale;
import com.hackaton.crm.party.client.SupplierClient;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactSummaryReaderService implements ContactSummaryReader {

    static final String OPEN_ORDER_STATUS = "VALIDATED";
    private static final long SERVER_DAY_CUT_MARGIN_DAYS = 1;

    private final ContactSource contactSource;
    private final CustomerClient customerClient;
    private final SupplierClient supplierClient;
    private final Clock clock;

    @Override
    public ContactSummaryResponse summarize() {
        List<ContactResponse> contacts = contactSource.load(ContactKind.ALL);
        return new ContactSummaryResponse(
                activeCustomersThisQuarter(),
                countMatching(contacts, CUSTOMER),
                countMatching(contacts, SUPPLIER),
                supplierClient.findOrdersByStatus(OPEN_ORDER_STATUS).size(),
                countMatching(contacts, FOLLOW_UP),
                contacts.stream().map(ContactResponse::outstanding).filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                countMatching(contacts, ContactResponse::overLimit));
    }

    private long activeCustomersThisQuarter() {
        LocalDate quarterStart = QuarterStart.of(LocalDate.now(clock));
        Instant since = quarterStart.atStartOfDay(clock.getZone()).toInstant();
        return customerClient.findValidatedSalesSince(quarterStart.minusDays(SERVER_DAY_CUT_MARGIN_DAYS)).stream()
                .filter(sale -> sale.customerId() != null && sale.validatedAt() != null)
                .filter(sale -> !sale.validatedAt().isBefore(since))
                .map(RemoteCustomerSale::customerId)
                .distinct()
                .count();
    }
}
