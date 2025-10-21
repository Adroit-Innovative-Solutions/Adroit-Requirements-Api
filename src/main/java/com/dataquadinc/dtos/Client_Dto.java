package com.dataquadinc.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client_Dto {
    private String clientId;
    private String clientName;
    private String clientAddress;
    private String positionType;
    private int netPayment;
    private List<SupportingCustomerInfo> supportingCustomers;
    @Pattern(regexp = "^(https?:\\/\\/)?([\\w.-]+)+(:\\d+)?(\\/.*)?$",
            message = "Invalid website URL format")
    private String clientWebsiteUrl;
    @Pattern(regexp = "^(https?:\\/\\/)?([\\w.-]+)+(:\\d+)?(\\/.*)?$",
            message = "Invalid LinkedIn URL format")
    private String clientLinkedInUrl;
    private List<ClientDocumentDto> supportingDocuments =new ArrayList<>(); // Stores file names only
    private String onBoardedById;
    private String onBoardedByName;
    private String status;
    private String feedBack;
    private int numberOfRequirements;

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



    @JsonIgnore // Prevents file data from being included in API responses
    private byte[] documentData;


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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


    public byte[] getDocumentData() {
        return documentData;
    }

    public void setDocumentData(byte[] documentData) {
        this.documentData = documentData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}