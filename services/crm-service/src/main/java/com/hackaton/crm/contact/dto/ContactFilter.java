package com.hackaton.crm.contact.dto;

public record ContactFilter(ContactKind kind, String search, boolean followUp, boolean professional) {
}
