package com.dataquadinc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class SubmissionsMultiDocs {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String fileName;
        private String contentType;  // MIME type
        private long size;
        private LocalDateTime uploadedAt;
        @Lob
        @Column(columnDefinition = "LONGBLOB")
        private byte[] data;   // ← actual file bytes

        private String submissionId;
}

