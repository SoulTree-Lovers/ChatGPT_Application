package org.example.link;

import org.example.link.entity.SummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {


}
