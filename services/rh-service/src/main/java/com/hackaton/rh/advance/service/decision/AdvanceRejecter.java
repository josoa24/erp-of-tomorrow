package com.hackaton.rh.advance.service.decision;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.common.security.Actor;

public interface AdvanceRejecter {

    AdvanceResponse reject(Long id, Actor actor);
}
