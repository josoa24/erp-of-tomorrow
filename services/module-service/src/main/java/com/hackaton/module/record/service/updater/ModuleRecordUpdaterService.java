package com.hackaton.module.record.service.updater;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ImmutableResourceException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.entity.ModuleStatus;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.mapper.ModuleRecordMapper;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModuleRecordUpdaterService implements ModuleRecordUpdater {

    private final DefinitionLookup definitionLookup;
    private final ModuleRecordRepository recordRepository;
    private final ModuleRecordMapper recordMapper;
    private final Clock clock;

    @Override
    @Transactional
    public ModuleRecordResponse advance(String slug, Long recordId) {
        ModuleDefinition definition = definitionLookup.require(slug);
        ModuleRecord record = recordRepository.findByIdAndDefinitionId(recordId, definition.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Enregistrement introuvable avec l'id " + recordId));
        List<ModuleStatus> statuses = definition.getStatuses();
        int current = IntStream.range(0, statuses.size())
                .filter(index -> statuses.get(index).getKey().equals(record.getStatus()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Statut actuel inconnu : " + record.getStatus()));
        if (statuses.get(current).isFinalStatus()) {
            throw new ImmutableResourceException("Statut final " + statuses.get(current).getLabel()
                    + " : l'enregistrement ne peut plus changer");
        }
        if (current == statuses.size() - 1) {
            throw new BusinessException("Aucun statut après " + statuses.get(current).getLabel());
        }
        record.setStatus(statuses.get(current + 1).getKey());
        record.setUpdatedAt(Instant.now(clock));
        return recordMapper.toResponse(recordRepository.save(record));
    }
}
