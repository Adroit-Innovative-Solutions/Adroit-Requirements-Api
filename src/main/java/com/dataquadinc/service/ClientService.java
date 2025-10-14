package com.dataquadinc.service;

import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ClientAlreadyExistsException;
import com.dataquadinc.model.Client;
import com.dataquadinc.repository.ClientRepository;
import com.dataquadinc.repository.RequirementRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);


    @Autowired
    private ClientRepository repository;

    @Autowired
    private ClientRepository repo;

    @Autowired
    private RequirementRepository requirementsDao;

    private String generateCustomId() {
        logger.info("Generating custom ID for new client...");
        List<Client> clients = repository.findAll();
        int maxNumber = clients.stream()
                .map(client -> {
                    try {
                        return Integer.parseInt(client.getId().replace("CLIENT", ""));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid client ID format: {}", client.getId());
                        return 0;
                    }
                })
                .max(Integer::compare)
                .orElse(0);

        String newId = String.format("CLIENT%03d", maxNumber + 1);
        logger.info("Generated new client ID: {}", newId);
        return newId;
    }

    private Client_Dto convertToDTO(Client client) {
        logger.debug("Converting Client entity to DTO for clientId: {}", client.getId());
        Client_Dto dto = new Client_Dto();
        dto.setId(client.getId());
        dto.setClientName(client.getClientName());
        dto.setClientAddress(client.getClientAddress());
        dto.setNetPayment(client.getNetPayment());
        dto.setSupportingCustomers(client.getSupportingCustomers());
        dto.setClientWebsiteUrl(client.getClientWebsiteUrl());
        dto.setClientLinkedInUrl(client.getClientLinkedInUrl());
        dto.setDocumentData(client.getDocumentedData());
        dto.setSupportingDocuments(client.getSupportingDocuments());
        dto.setOnBoardedBy(client.getOnBoardedBy());
        dto.setPositionType(client.getPositionType());
        dto.setStatus(client.getStatus());
        dto.setFeedBack(client.getFeedBack());
        return dto;
    }

    private ClientsDetailsDto convertToClientsDTO(Client clients) {
        logger.debug("Converting Client entity to DTO for clientId: {}", clients.getId());
        ClientsDetailsDto dto = new ClientsDetailsDto();
        dto.setId(clients.getId());
        dto.setClientName(clients.getClientName());
        return dto;
    }

    private Client convertToEntity(Client_Dto dto) {
        logger.debug("Converting Client DTO to entity for clientName: {}", dto.getClientName());
        Client client = new Client();
        client.setId(dto.getId());
        client.setClientName(dto.getClientName());
        client.setClientAddress(dto.getClientAddress());
        client.setNetPayment(dto.getNetPayment());
        client.setSupportingCustomers(dto.getSupportingCustomers());
        client.setClientWebsiteUrl(dto.getClientWebsiteUrl());
        client.setClientLinkedInUrl(dto.getClientLinkedInUrl());
        client.setDocumentedData(dto.getDocumentData());
        client.setSupportingDocuments(dto.getSupportingDocuments());
        client.setOnBoardedBy(dto.getOnBoardedBy());
        client.setPositionType(dto.getPositionType());
        client.setFeedBack(dto.getFeedBack());
        return client;
    }

    public Client_Dto createClient(Client_Dto dto, List<MultipartFile> files) throws IOException {
        logger.info("Creating new client with name: {}", dto.getClientName());

        if (!repo.findByClientName(dto.getClientName()).isEmpty()) {
            logger.error("Client already exists: {}", dto.getClientName());
            throw new ClientAlreadyExistsException("Client Name '" + dto.getClientName() + "' already exists.");
        }

        Client entity = convertToEntity(dto);
        entity.setId(generateCustomId());

        String createdBy = dto.getOnBoardedBy();
        String assignedTo = dto.getAssignedTo();

        if (assignedTo != null && !assignedTo.isBlank()) {
            logger.info("Setting assignedTo for client: {}", assignedTo);
            entity.setOnBoardedBy(assignedTo);
        } else {
            logger.info("Setting createdBy for client: {}", createdBy);
            entity.setOnBoardedBy(createdBy);
        }

        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            logger.info("Creating upload directory: {}", uploadDir.toString());
            Files.createDirectories(uploadDir);
        }

        List<String> fileNames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            logger.info("Uploading {} supporting documents for client: {}", files.size(), dto.getClientName());
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = file.getOriginalFilename();
                    fileNames.add(fileName);

                    Path filePath = uploadDir.resolve(fileName);
                    Files.write(filePath, file.getBytes());
                    logger.info("Saved file {} for client {}", fileName, dto.getClientName());
                }
            }
        }
        entity.setSupportingDocuments(fileNames);

        entity = repo.save(entity);
        logger.info("Client created successfully with ID: {}", entity.getId());
        return convertToDTO(entity);
    }

    public List<Client_Dto> getAllClients() {
        logger.info("Fetching all clients for the current month...");
        List<Client> clients = repository.getClients();
        logger.info("Fetched {} clients for current month", clients.size());

        return clients.stream()
                .map(client -> {
                    Client_Dto dto = convertToDTO(client);
                    int requirementCount = repository.countRequirementsByClientName(client.getClientName());
                    dto.setNumberOfRequirements(requirementCount);
                    logger.debug("Client {} has {} requirements", client.getId(), requirementCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ClientsDetailsDto> getAllClientsNames() {
        logger.info("Fetching all clients for the current month...");
        List<Client> clients = repository.getClients();
        logger.info("Fetched {} clients for current month", clients.size());

        return clients.stream()
                .map(client -> {
                    ClientsDetailsDto dto = convertToClientsDTO(client);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void evaluateClientStatuses() {
        logger.info("Evaluating client statuses for the past 30 days...");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Client> allClients = repository.findAll();

        for (Client client : allClients) {
            boolean hasRecentJob = requirementsDao.existsByClientNameAndCreatedAtAfter(
                    client.getClientName(), thirtyDaysAgo);

            String status = hasRecentJob ? "ACTIVE" : "INACTIVE";
            client.setStatus(status);
            logger.debug("Client {} (name: {}) status evaluated to: {}", client.getId(), client.getClientName(), status);
        }

        repository.saveAll(allClients);
        logger.info("Client statuses updated for all clients.");
    }


    public Optional<Client_Dto> getClientById(String id) {
        logger.info("Fetching client by ID: {}", id);
        Optional<Client> clientOpt = repository.findById(id);
        if (clientOpt.isPresent()) {
            logger.info("Client found with ID: {}", id);
        } else {
            logger.warn("No client found with ID: {}", id);
        }
        return clientOpt.map(this::convertToDTO);
    }

    public Optional<Client_Dto> updateClient(String id, Client_Dto dto, List<MultipartFile> files) {
        logger.info("Updating client with ID: {}", id);
        return repository.findById(id).map(existingClient -> {

            String createdBy = dto.getOnBoardedBy();
            String assignedTo = dto.getAssignedTo();
            if (assignedTo != null && !assignedTo.isBlank()) {
                logger.info("Assigned client to: {}", assignedTo);
                existingClient.setOnBoardedBy(assignedTo);
            } else if (createdBy != null && !createdBy.isBlank()) {
                logger.info("Updated client by: {}", createdBy);
                existingClient.setOnBoardedBy(createdBy);
            }

            if (dto.getClientName() != null) {
                logger.debug("Updating clientName to: {}", dto.getClientName());
                existingClient.setClientName(dto.getClientName());
            }
            if (dto.getClientAddress() != null) {
                logger.debug("Updating clientAddress to: {}", dto.getClientAddress());
                existingClient.setClientAddress(dto.getClientAddress());
            }
            if (dto.getNetPayment() != 0) {
                logger.debug("Updating netPayment to: {}", dto.getNetPayment());
                existingClient.setNetPayment(dto.getNetPayment());
            }
            if (dto.getClientWebsiteUrl() != null) {
                logger.debug("Updating clientWebsiteUrl to: {}", dto.getClientWebsiteUrl());
                existingClient.setClientWebsiteUrl(dto.getClientWebsiteUrl());
            }
            if (dto.getClientLinkedInUrl() != null) {
                logger.debug("Updating clientLinkedInUrl to: {}", dto.getClientLinkedInUrl());
                existingClient.setClientLinkedInUrl(dto.getClientLinkedInUrl());
            }
            if (dto.getPositionType() != null) {
                logger.debug("Updating positionType to: {}", dto.getPositionType());
                existingClient.setPositionType(dto.getPositionType());
            }
            if (dto.getSupportingCustomers() != null) {
                logger.debug("Updating supportingCustomers for clientId: {}", id);
                existingClient.setSupportingCustomers(dto.getSupportingCustomers());
            }
            if (dto.getFeedBack() != null) {
                logger.debug("Updating feedBack to: {}", dto.getFeedBack());
                existingClient.setFeedBack(dto.getFeedBack());
            }
            try {
                if (files != null && !files.isEmpty()) {
                    logger.info("Uploading {} additional documents for client update: {}", files.size(), id);
                    Path uploadDir = Paths.get("uploads");
                    if (!Files.exists(uploadDir)) {
                        logger.info("Creating upload directory: {}", uploadDir.toString());
                        Files.createDirectories(uploadDir);
                    }

                    List<String> fileNames = new ArrayList<>(existingClient.getSupportingDocuments());

                    for (MultipartFile file : files) {
                        if (!file.isEmpty()) {
                            String fileName = file.getOriginalFilename();
                            fileNames.add(fileName);

                            Path filePath = uploadDir.resolve(fileName);
                            Files.write(filePath, file.getBytes());
                            logger.info("Saved file {} for client {}", fileName, id);
                        }
                    }
                    existingClient.setSupportingDocuments(fileNames);
                }
            } catch (IOException e) {
                logger.error("Error while uploading files for client update: {}", e.getMessage());
                e.printStackTrace();
            }

            Client saved = repository.save(existingClient);
            logger.info("Client updated successfully: {}", saved.getId());
            return convertToDTO(saved);
        });
    }


    public void deleteClient(String id) {
        logger.info("Deleting client with ID: {}", id);
        repository.deleteById(id);
        logger.info("Client deleted: {}", id);
    }
}
