package com.hackaton.module.definition.mapper;

import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.dto.ModuleSummary;
import com.hackaton.module.definition.entity.ModuleDefinition;
import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModuleDefinitionMapper {

    private final DefinitionPartsMapper partsMapper;

    public ModuleSummary toSummary(ModuleDefinition definition) {
        return new ModuleSummary(definition.getId(), definition.getSlug(), definition.getName(),
                definition.getIcon(), definition.getCreatedAt());
    }

    public ModuleDefinitionResponse toResponse(ModuleDefinition definition) {
        return new ModuleDefinitionResponse(definition.getId(), definition.getSlug(), definition.getName(),
                definition.getIcon(), definition.getDescription(),
                definition.getColumns().stream().map(partsMapper::toSpec).toList(),
                definition.getStatuses().stream().map(partsMapper::toSpec).toList(),
                definition.getIndicators().stream().map(partsMapper::toSpec).toList(),
                definition.getCreatedBy(), definition.getCreatedAt());
    }

    public ModuleDefinition toEntity(ModuleDefinitionRequest request, String slug, String author, Instant now) {
        ModuleDefinition definition = new ModuleDefinition();
        definition.setSlug(slug);
        definition.setName(request.name().trim());
        definition.setIcon(request.icon());
        definition.setDescription(blankToNull(request.description()));
        definition.setCreatedBy(author);
        definition.setCreatedAt(now);
        positioned(request.columns(), partsMapper::toEntity).forEach(column -> {
            column.setDefinition(definition);
            definition.getColumns().add(column);
        });
        positioned(request.statuses(), partsMapper::toEntity).forEach(status -> {
            status.setDefinition(definition);
            definition.getStatuses().add(status);
        });
        positioned(request.indicators(), partsMapper::toEntity).forEach(indicator -> {
            indicator.setDefinition(definition);
            definition.getIndicators().add(indicator);
        });
        return definition;
    }

    private <S, E> List<E> positioned(List<S> specs, BiFunction<S, Integer, E> mapper) {
        if (specs == null) {
            return List.of();
        }
        return IntStream.range(0, specs.size()).mapToObj(index -> mapper.apply(specs.get(index), index)).toList();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
