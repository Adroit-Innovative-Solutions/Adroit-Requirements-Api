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
    @Column(name = "vendor_address")
    private String vendorAddress;

    private String positionType;
    private int netPayment;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<SupportingCustomerInfo> supportingCustomers = new ArrayList<>();

    @Column(name = "vendor_website_url")
    private String vendorWebsiteUrl;

    @Column(name = "vendor_linked_in_url")
    private String vendorLinkedInUrl;

    @Column(name = "invoice", length = 10)
    private String invoice;

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
    private int numberOfRequirements;// Computed, not persisted

    @Column(name = "vendor_id", unique = true)
    private String vendorId;

    @Column(name = "vendor_name")
    private String vendorName;

//    @Column(name = "vendor_net_pay")
//    private int vendorNetPay;

    @Column(name = "net_pay")
    private int netPay;



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

    public String getPositionType() {
        return positionType;
    }

    public String getVendorAddress() { return vendorAddress; }

    public void setVendorAddress(String vendorAddress) { this.vendorAddress = vendorAddress; }

    public String getVendorWebsiteUrl() { return vendorWebsiteUrl; }

    public void setVendorWebsiteUrl(String vendorWebsiteUrl) { this.vendorWebsiteUrl = vendorWebsiteUrl; }

    public String getVendorLinkedInUrl() { return vendorLinkedInUrl; }

    public void setVendorLinkedInUrl(String vendorLinkedInUrl) { this.vendorLinkedInUrl = vendorLinkedInUrl; }

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

    public String getVendorId() {return vendorId;}

    public void setVendorId(String vendorId) {this.vendorId = vendorId;}

    public String getVendorName() {return vendorName;}

    public void setVendorName(String vendorName) {this.vendorName = vendorName;}

//    public int getVendorNetPay() { return vendorNetPay; }
//
//    public void setVendorNetPay(int vendorNetPay) { this.vendorNetPay = vendorNetPay;}

    public String getInvoice() { return invoice; }

    public void setInvoice(String invoice) { this.invoice = invoice; }

    public int getNetPay() { return netPay; }

    public void setNetPay(int netPay) { this.netPay = netPay; }
}