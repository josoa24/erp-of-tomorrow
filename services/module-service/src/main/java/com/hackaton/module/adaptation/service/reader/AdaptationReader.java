package com.hackaton.module.adaptation.service.reader;

import com.hackaton.module.adaptation.dto.AdaptationResponse;
import java.util.List;

public interface AdaptationReader {

    List<AdaptationResponse> findAll();
}
