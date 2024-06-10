package org.example.link.domain.summary.converter;

import org.example.link.domain.summary.controller.dto.SummaryResponse;
import org.example.link.domain.summary.repository.SummaryEntity;
import org.springframework.stereotype.Component;

@Component
public class SummaryConverter {

    public SummaryResponse toSummaryResponse(SummaryEntity summaryEntity) {

        return SummaryResponse.builder()
            .url(summaryEntity.getUrl())
            .summaryContent(summaryEntity.getSummary())
            .build();
    }
}

