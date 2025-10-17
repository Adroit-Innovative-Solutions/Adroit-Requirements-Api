package com.dataquadinc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ClientDocument_US")
public class ClientDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;     // e.g., uploads/{clientId}/{fileName}
    private String contentType;  // MIME type
    private long size;
    private LocalDateTime uploadedAt;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;   // ← actual file bytes
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "clientId")
    private Client client;
}

