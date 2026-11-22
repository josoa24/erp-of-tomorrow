package com.hackaton.module.reference.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteEmployee(Long id, String firstName, String lastName) {
}
