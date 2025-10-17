package com.dataquadinc.model;

import com.dataquadinc.dtos.SupportingCustomerInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Client_US")
public class Client {

    @Id
    private String clientId;  // Custom-generated ID

    @Column(unique = true, nullable = false)
    private String clientName;

    private String onBoardedById;
    private String onBoardedByName;
    private String clientAddress;
    private String positionType;
    private int netPayment;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<SupportingCustomerInfo> supportingCustomers = new ArrayList<>();

    private String clientWebsiteUrl;

    @Column(length = 1000)
    private String clientLinkedInUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> supportingDocumentNames = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientDocument> documents = new ArrayList<>();

    private String status;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] documentedData;  // Optional: actual file content

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String feedBack;

    @Transient
    private int numberOfRequirements; // Computed, not persisted

    // Auto-generate clientId if not provided
    @PrePersist
    public void prePersist() {
        if (this.clientId == null || this.clientId.isEmpty()) {
            this.clientId = "BDM" + UUID.randomUUID().toString().substring(0, 8);
        }
    }

    // Computed transient getter for document file names
    @Transient
    public List<String> getDocumentFileNames() {
        return documents.stream()
                .map(ClientDocument::getFileName)
                .toList();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getOnBoardedById() {
        return onBoardedById;
    }

    public void setOnBoardedById(String onBoardedById) {
        this.onBoardedById = onBoardedById;
    }

    public String getOnBoardedByName() {
        return onBoardedByName;
    }

    public void setOnBoardedByName(String onBoardedByName) {
        this.onBoardedByName = onBoardedByName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public int getNetPayment() {
        return netPayment;
    }

    public void setNetPayment(int netPayment) {
        this.netPayment = netPayment;
    }

    public List<SupportingCustomerInfo> getSupportingCustomers() {
        return supportingCustomers;
    }

    public void setSupportingCustomers(List<SupportingCustomerInfo> supportingCustomers) {
        this.supportingCustomers = supportingCustomers;
    }

    public String getClientWebsiteUrl() {
        return clientWebsiteUrl;
    }

    public void setClientWebsiteUrl(String clientWebsiteUrl) {
        this.clientWebsiteUrl = clientWebsiteUrl;
    }

    public String getClientLinkedInUrl() {
        return clientLinkedInUrl;
    }

    public void setClientLinkedInUrl(String clientLinkedInUrl) {
        this.clientLinkedInUrl = clientLinkedInUrl;
    }

    public List<String> getSupportingDocumentNames() {
        return supportingDocumentNames;
    }

    public void setSupportingDocumentNames(List<String> supportingDocumentNames) {
        this.supportingDocumentNames = supportingDocumentNames;
    }

    public List<ClientDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ClientDocument> documents) {
        this.documents = documents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getDocumentedData() {
        return documentedData;
    }

    public void setDocumentedData(byte[] documentedData) {
        this.documentedData = documentedData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public int getNumberOfRequirements() {
        return numberOfRequirements;
    }

    public void setNumberOfRequirements(int numberOfRequirements) {
        this.numberOfRequirements = numberOfRequirements;
    }
}