package com.hackaton.crm.party.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteCustomerSale(Long customerId, Instant validatedAt) {
}
