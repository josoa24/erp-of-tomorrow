package com.hackaton.crm.party.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteSupplier(
        Long id, String name, String phone, String email, String city, String nif, String stat, Instant createdAt) {
}
