package com.hackaton.vente.cancellation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancellationRequest(@NotBlank @Size(max = 500) String reason) {
}
