package com.dataquadinc.model;

import com.dataquadinc.dtos.SupportingCustomerInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Client_US")
public class Client {

    @Id
    private String id;  // Custom-generated ID
    @Column(unique = true, nullable = false)
    private String clientName;
    private String onBoardedBy;
    private String clientAddress;
    private String positionType;
    private int netPayment;


    @JdbcTypeCode(SqlTypes.JSON)
    private List<SupportingCustomerInfo> supportingCustomers;

    private String clientWebsiteUrl;
    @Column(length = 1000)
    private String clientLinkedInUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> supportingDocuments;

    @Column
    private String status;

    @Transient
    private int numberOfRequirements; // 👈 won't be persisted


    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] documentedData;  // Stores actual file content

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = "BDM" + UUID.randomUUID().toString().substring(0, 8);  // Generate Unique ID
        }
    }
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String feedBack;

    public int getNumberOfRequirements() {
        return numberOfRequirements;
    }

    public void setNumberOfRequirements(int numberOfRequirements) {
        this.numberOfRequirements = numberOfRequirements;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getOnBoardedBy() {
        return onBoardedBy;
    }

    public void setOnBoardedBy(String onBoardedBy) {
        this.onBoardedBy = onBoardedBy;
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

    public List<String> getSupportingDocuments() {
        return supportingDocuments;
    }

    public void setSupportingDocuments(List<String> supportingDocuments) {
        this.supportingDocuments = supportingDocuments;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}