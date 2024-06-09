package org.example.link;

import lombok.RequiredArgsConstructor;
import org.example.link.entity.SummaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;

    public SummaryEntity saveSummary(String url, String summary) {

        return summaryRepository.save(SummaryEntity.builder()
            .url(url)
            .summary(summary)
            .build());

    }

    public List<SummaryEntity> getAllSummaries() {
        return summaryRepository.findAll();
    }
}
