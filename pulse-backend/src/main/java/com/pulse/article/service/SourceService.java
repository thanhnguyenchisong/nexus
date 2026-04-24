package com.pulse.article.service;

import com.pulse.article.dto.SourceDto;
import com.pulse.article.dto.SourceUpdateRequest;
import com.pulse.article.entity.Source;
import com.pulse.article.repository.SourceRepository;
import com.pulse.collector.service.CollectorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SourceService {

    @Inject
    SourceRepository sourceRepository;

    @Inject
    CollectorService collectorService;

    public List<SourceDto> getAllSources() {
        return sourceRepository.listAll().stream().map(SourceDto::from).toList();
    }

    public List<SourceDto> getSourcesByDomain(String domain) {
        return sourceRepository.find("domain", domain.toUpperCase()).list()
                .stream().map(SourceDto::from).toList();
    }

    public SourceDto getById(Long id) {
        Source s = sourceRepository.findById(id);
        return s != null ? SourceDto.from(s) : null;
    }

    @Transactional
    public SourceDto update(Long id, SourceUpdateRequest req) {
        Source s = sourceRepository.findById(id);
        if (s == null) return null;

        if (req.status() != null && !req.status().isBlank()) {
            s.status = req.status().toUpperCase();
            // Reset error count when re-activating
            if ("ACTIVE".equals(s.status)) s.errorCount = 0;
        }
        if (req.fetchIntervalMinutes() != null && req.fetchIntervalMinutes() > 0) {
            s.fetchIntervalMinutes = req.fetchIntervalMinutes();
        }
        sourceRepository.persist(s);
        return SourceDto.from(s);
    }

    public boolean triggerFetch(Long id) {
        Source s = sourceRepository.findById(id);
        if (s == null) return false;
        collectorService.collectSource(s);
        return true;
    }
}
