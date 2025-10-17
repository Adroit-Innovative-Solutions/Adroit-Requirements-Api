package com.dataquadinc.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ClientDocumentDto {
    private Long id;
    private String fileName;
    private String filePath;
    private String contentType;
    private long size;
    private LocalDateTime uploadedAt;
}

