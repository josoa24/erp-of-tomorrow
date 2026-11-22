package com.hackaton.module.adaptation.repository;

import com.hackaton.module.adaptation.entity.AdaptationActivation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdaptationActivationRepository extends JpaRepository<AdaptationActivation, Long> {

    List<AdaptationActivation> findAllByOrderByActivatedAtAscIdAsc();

    Optional<AdaptationActivation> findByAdaptationKey(String adaptationKey);

    boolean existsByAdaptationKey(String adaptationKey);
}
