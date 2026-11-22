package com.hackaton.module.adaptation.service.deleter;

import com.hackaton.module.common.security.Caller;

public interface AdaptationDeleter {

    void deactivate(String adaptationKey, Caller caller);
}
