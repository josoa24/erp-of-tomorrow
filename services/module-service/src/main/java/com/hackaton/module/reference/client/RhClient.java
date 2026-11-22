package com.hackaton.module.reference.client;

import java.util.Optional;

public interface RhClient {

    Optional<RemoteEmployee> findEmployee(Long id);
}
