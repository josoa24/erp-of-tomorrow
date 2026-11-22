package com.hackaton.module.reference.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VenteClient {

    Optional<RemoteNamed> findCustomer(Long id);

    Optional<RemoteSale> findSale(Long id);

    List<RemoteSale> findValidatedSalesToDeliver(LocalDate from, LocalDate to);
}
