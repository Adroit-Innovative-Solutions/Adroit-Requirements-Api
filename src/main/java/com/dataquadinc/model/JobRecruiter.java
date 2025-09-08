package com.dataquadinc.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="job_recruiters_us")
public class JobRecruiter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String assignedBy;
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Requirement requirement;

}
