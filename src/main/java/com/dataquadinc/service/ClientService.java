package com.dataquadinc.service;

import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ClientAlreadyExistsException;
import com.dataquadinc.model.Client;
import com.dataquadinc.model.ClientDocument;
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
                        return Integer.parseInt(client.getClientId().replace("CLIENT", ""));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid client ID format: {}", client.getClientId());
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
        logger.debug("Converting Client entity to DTO for clientId: {}", client.getClientId());
        Client_Dto dto = new Client_Dto();
        dto.setClientId(client.getClientId());
        dto.setClientName(client.getClientName());
        dto.setClientAddress(client.getClientAddress());
        dto.setNetPayment(client.getNetPayment());
        dto.setSupportingCustomers(client.getSupportingCustomers());
        dto.setClientWebsiteUrl(client.getClientWebsiteUrl());
        dto.setClientLinkedInUrl(client.getClientLinkedInUrl());
        dto.setOnBoardedById(client.getOnBoardedById());
        dto.setOnBoardedByName(client.getOnBoardedByName());
        dto.setPositionType(client.getPositionType());
        dto.setStatus(client.getStatus());
        dto.setFeedBack(client.getFeedBack());
        List<ClientDocumentDto> documentDtos = client.getDocuments().stream()
                .map(doc -> {
                    ClientDocumentDto d = new ClientDocumentDto();
                    d.setId(doc.getId());
                    d.setFileName(doc.getFileName());
                    d.setFilePath(doc.getFilePath());
                    d.setContentType(doc.getContentType());
                    d.setSize(doc.getSize());
                    d.setUploadedAt(doc.getUploadedAt());
                    return d;
                })
                .collect(Collectors.toList());

        // ✅ Add only file names (or modify to send full documentDtos if desired)
        dto.setSupportingDocuments(documentDtos);
        return dto;
    }

    private ClientsDetailsDto convertToClientsDTO(Client clients) {
        logger.debug("Converting Client entity to DTO for clientId: {}", clients.getClientId());
        ClientsDetailsDto dto = new ClientsDetailsDto();
        dto.setId(clients.getClientId());
        dto.setClientName(clients.getClientName());
        return dto;
    }

    private Client convertToEntity(Client_Dto dto) {
        logger.debug("Converting Client DTO to entity for clientName: {}", dto.getClientName());
        Client client = new Client();
        client.setClientId(dto.getClientId());
        client.setClientName(dto.getClientName());
        client.setClientAddress(dto.getClientAddress());
        client.setNetPayment(dto.getNetPayment());
        client.setSupportingCustomers(dto.getSupportingCustomers());
        client.setClientWebsiteUrl(dto.getClientWebsiteUrl());
        client.setClientLinkedInUrl(dto.getClientLinkedInUrl());
        client.setOnBoardedById(dto.getOnBoardedById());
        client.setOnBoardedByName(dto.getOnBoardedByName());
        client.setPositionType(dto.getPositionType());
        client.setFeedBack(dto.getFeedBack());
        // Map supporting documents from DTO to entity
        if (dto.getSupportingDocuments() != null && !dto.getSupportingDocuments().isEmpty()) {
            List<ClientDocument> documents = dto.getSupportingDocuments().stream().map(docDto -> {
                ClientDocument doc = new ClientDocument();
                doc.setId(docDto.getId());
                doc.setFileName(docDto.getFileName());
                doc.setFilePath(docDto.getFilePath());
                doc.setContentType(docDto.getContentType());
                doc.setSize(docDto.getSize());
                doc.setUploadedAt(docDto.getUploadedAt());
                doc.setClient(client); // associate with parent client
                return doc;
            }).collect(Collectors.toList());

            client.setDocuments(documents);

            // Optional: maintain just file names separately if needed
            client.setSupportingDocumentNames(
                    documents.stream().map(ClientDocument::getFileName).collect(Collectors.toList())
            );
        } else {
            client.setDocuments(new ArrayList<>());
            client.setSupportingDocumentNames(new ArrayList<>());
        }
        return client;
    }

    public Client_Dto createClient(Client_Dto dto, List<MultipartFile> files) throws IOException {
        logger.info("Creating new client with name: {}", dto.getClientName());

        if (!repo.findByClientName(dto.getClientName()).isEmpty()) {
            logger.error("Client already exists: {}", dto.getClientName());
            throw new ClientAlreadyExistsException("Client Name '" + dto.getClientName() + "' already exists.");
        }

        Client entity = convertToEntity(dto);
        entity.setClientId(generateCustomId());

        String createdById = dto.getOnBoardedById();
        String createdByName = dto.getOnBoardedByName();


        if (createdById != null && !createdById.isBlank()) {
            logger.info("Setting createdBy (ID: {}, Name: {})", createdById,createdByName);
            entity.setOnBoardedById(createdById);
            entity.setOnBoardedByName(createdByName);
        } else {
            logger.warn("No onboarding info provided for client: {}", dto.getClientName());
        }


        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        if (files != null && !files.isEmpty()) {
            logger.info("Uploading {} supporting documents for client: {}", files.size(), dto.getClientName());
            List<ClientDocument> documentEntities = new ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(fileName);
                    Files.write(filePath, file.getBytes());

                    ClientDocument doc = new ClientDocument();
                    doc.setFileName(fileName);
                    doc.setFilePath(filePath.toString());
                    doc.setContentType(file.getContentType());
                    doc.setSize(file.getSize());
                    doc.setUploadedAt(LocalDateTime.now());
                    doc.setData(file.getBytes());
                    doc.setClient(entity);

                    documentEntities.add(doc);
                    logger.info("Saved file {} for client {}", fileName, dto.getClientName());
                }
            }

            entity.setDocuments(documentEntities);
            entity.setSupportingDocumentNames(
                    documentEntities.stream().map(ClientDocument::getFileName).collect(Collectors.toList())
            );
        }
        // ✅ End of block

        // Now save the client entity
        Client saved = repository.save(entity);
        logger.info("Client created successfully with ID: {}", saved.getClientId());

        return convertToDTO(saved);
    }

    public List<Client_Dto> getAllClients() {
        logger.info("Fetching all clients with their documents...");
        List<Client> clients = repository.findAllWithDocuments();

        return clients.stream()
                .map(client -> {
                    Client_Dto dto = convertToDTO(client);
                    int requirementCount = repository.countRequirementsByClientName(client.getClientName());
                    dto.setNumberOfRequirements(requirementCount);
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
            logger.debug("Client {} (name: {}) status evaluated to: {}", client.getClientId(), client.getClientName(), status);
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

            // 🔹 Update onboarded info (only if provided)
            if (dto.getOnBoardedById() != null && !dto.getOnBoardedById().isBlank()) {
                existingClient.setOnBoardedById(dto.getOnBoardedById());
                existingClient.setOnBoardedByName(dto.getOnBoardedByName());
                logger.debug("Updated onboarded info for client: ID={}, Name={}", dto.getOnBoardedById(), dto.getOnBoardedByName());
            }

            // 🔹 Update core fields only if new values are provided
            if (dto.getClientName() != null)
                existingClient.setClientName(dto.getClientName());

            if (dto.getClientAddress() != null)
                existingClient.setClientAddress(dto.getClientAddress());

            if (dto.getNetPayment() > 0)
                existingClient.setNetPayment(dto.getNetPayment());

            if (dto.getClientWebsiteUrl() != null)
                existingClient.setClientWebsiteUrl(dto.getClientWebsiteUrl());

            if (dto.getClientLinkedInUrl() != null)
                existingClient.setClientLinkedInUrl(dto.getClientLinkedInUrl());

            if (dto.getPositionType() != null)
                existingClient.setPositionType(dto.getPositionType());

            if (dto.getSupportingCustomers() != null)
                existingClient.setSupportingCustomers(dto.getSupportingCustomers());

            if (dto.getFeedBack() != null)
                existingClient.setFeedBack(dto.getFeedBack());

            // 🔹 Handle file uploads
            try {
                if (files != null && !files.isEmpty()) {
                    logger.info("Uploading {} new document(s) for client update: {}", files.size(), id);

                    Path uploadDir = Paths.get("uploads");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                        logger.debug("Created upload directory: {}", uploadDir.toAbsolutePath());
                    }

                    // Use the proper list from entity (rename accordingly)
                    List<String> updatedDocNames = new ArrayList<>(
                            existingClient.getSupportingDocumentNames() != null
                                    ? existingClient.getSupportingDocumentNames()
                                    : new ArrayList<>()
                    );

                    for (MultipartFile file : files) {
                        if (!file.isEmpty()) {
                            String fileName = file.getOriginalFilename();
                            Path filePath = uploadDir.resolve(fileName);

                            Files.write(filePath, file.getBytes());
                            updatedDocNames.add(fileName);

                            logger.debug("Uploaded file '{}' for client '{}'", fileName, id);

                            // Optionally, create ClientDocument entity for detailed info
                            ClientDocument document = new ClientDocument();
                            document.setFileName(fileName);
                            document.setFilePath(filePath.toString());
                            document.setContentType(file.getContentType());
                            document.setSize(file.getSize());
                            document.setUploadedAt(LocalDateTime.now());
                            document.setClient(existingClient);

                            existingClient.getDocuments().add(document);
                        }
                    }

                    // Save file name list
                    existingClient.setSupportingDocumentNames(updatedDocNames);
                }
            } catch (IOException e) {
                logger.error("Error uploading files for client update: {}", e.getMessage(), e);
            }

            // 🔹 Save updated entity
            Client updatedClient = repository.save(existingClient);
            logger.info("Client updated successfully: {}", updatedClient.getClientId());

            // 🔹 Convert back to DTO for response
            return convertToDTO(updatedClient);
        });
    }


    public void deleteClient(String id) {
        logger.info("Deleting client with ID: {}", id);
        repository.deleteById(id);
        logger.info("Client deleted: {}", id);
    }
}
