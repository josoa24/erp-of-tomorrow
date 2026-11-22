package com.hackaton.module.reference.client;

import java.util.Optional;

public interface StockClient {

    Optional<RemoteNamed> findProduct(Long id);

    Optional<RemoteNamed> findSupplier(Long id);
}
