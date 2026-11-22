package com.hackaton.module.reference.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteNamed(Long id, String name) {
}
