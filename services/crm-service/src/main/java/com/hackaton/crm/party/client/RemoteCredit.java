package com.hackaton.crm.party.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteCredit(Long customerId, BigDecimal outstanding, Integer overdueCount) {
}
