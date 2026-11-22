package com.hackaton.module.record.service.reader;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.entity.ModuleStatus;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.dto.RecordPage;
import com.hackaton.module.record.dto.RecordQuery;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.mapper.ModuleRecordMapper;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleRecordReaderService implements ModuleRecordReader {

    static final int MAX_PAGE_SIZE = 100;

    private final DefinitionLookup definitionLookup;
    private final ModuleRecordRepository recordRepository;
    private final RecordSearchFilter searchFilter;
    private final ModuleRecordMapper recordMapper;

    @Override
    public RecordPage search(String slug, RecordQuery query) {
        if (query.page() < 0 || query.size() < 1 || query.size() > MAX_PAGE_SIZE) {
            throw new InvalidInputException("Pagination invalide : page >= 0 et taille entre 1 et " + MAX_PAGE_SIZE);
        }
        ModuleDefinition definition = definitionLookup.require(slug);
        String status = query.status() == null || query.status().isBlank() ? null : query.status().trim();
        if (status != null && definition.getStatuses().stream().map(ModuleStatus::getKey).noneMatch(status::equals)) {
            throw new InvalidInputException("Statut inconnu pour ce module : " + status);
        }
        List<ModuleRecord> matching = recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(definition.getId())
                .stream()
                .filter(record -> status == null || status.equals(record.getStatus()))
                .filter(record -> searchFilter.matches(record, definition.getColumns(), query.search()))
                .toList();
        List<ModuleRecord> page = matching.stream()
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .toList();
        return new RecordPage(page.stream().map(recordMapper::toResponse).toList(), matching.size());
    }
}
