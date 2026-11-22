package com.hackaton.module.salesimport.service;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.salesimport.dto.SalesImportResult;

public interface SalesImportCreator {

    SalesImportResult importSales(String slug, Caller caller);
}
