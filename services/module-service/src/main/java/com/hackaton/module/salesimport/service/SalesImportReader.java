package com.hackaton.module.salesimport.service;

import com.hackaton.module.salesimport.dto.SalesImportAvailability;

public interface SalesImportReader {

    SalesImportAvailability availability(String slug);
}
