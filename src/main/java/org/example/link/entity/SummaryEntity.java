package org.example.link.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "summary")
public class SummaryEntity extends BaseEntity {

    @Column(name = "varchar(255)", nullable = false)
    private String url;

    @Column(name = "text", nullable = false)
    private String summary;

}