package com.hackaton.module.adaptation.dto;

import java.time.Instant;

public record AdaptationResponse(String moduleKey, String adaptationKey, String activatedBy, Instant activatedAt) {
}
