package com.dataquadinc.controller;

import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.model.Client;
import com.dataquadinc.model.ClientDocument;
import com.dataquadinc.repository.ClientDocumentRepository;
import com.dataquadinc.repository.ClientRepository;
import com.dataquadinc.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin(origins = {"http://35.188.150.92", "http://192.168.0.140:3000", "http://192.168.0.139:3000","https://mymulya.com","http://localhost:3000",
        "http://192.168.0.135:80/","http://192.168.0.135/","http://182.18.177.16:443","http://mymulya.com:443","http://localhost/",
        "http://154.210.288.26/",
        "http://192.168.0.203:3000/",
        "http://192.168.1.144:3000/"})
@RestController
@RequestMapping(SystemConstants.API_BASE_PATH+"/requirements")
public class ClientController {

    @Autowired
    private ClientService service;
    @Autowired
    private ClientRepository repo;
    @Autowired
    private ClientDocumentRepository clientDocumentRepository;

    private final Path UPLOAD_DIR = Paths.get("uploads");

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/client/addClient")
    public ResponseEntity<ApiResponse<Client_Dto>> createClient(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "supportingDocuments", required = false) List<MultipartFile> files) {

        try {
            logger.info("Received DTO JSON: {}", dtoJson);

            Client_Dto dto = objectMapper.readValue(dtoJson, Client_Dto.class);
            Client_Dto createdClient = service.createClient(dto, files);
            return ResponseEntity.ok(new ApiResponse<>(true, "Client added successfully", createdClient, null));
        } catch (IOException e) {
            ErrorDto error = new ErrorDto("Invalid JSON", "Error parsing client data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Invalid JSON", null, error));
        }
    }

    @GetMapping("/client/getAll")
    public ResponseEntity<ApiResponse<List<Client_Dto>>> getAllClients() {
        service.evaluateClientStatuses();
        List<Client_Dto> clients = service.getAllClients();
        return ResponseEntity.ok(new ApiResponse<>(true, "Clients fetched successfully", clients, null));
    }

    @GetMapping("/client/getAllClientsNames")
    public ResponseEntity<ApiResponse<List<ClientsDetailsDto>>> getAllClientsNames() {
        service.evaluateClientStatuses();
        List<ClientsDetailsDto> clients = service.getAllClientsNames();
        return ResponseEntity.ok(new ApiResponse<>(true, "Clients fetched successfully", clients, null));
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<ApiResponse<Client_Dto>> getClientById(@PathVariable String id) {
        Optional<Client_Dto> optionalClient = service.getClientById(id);
        if (optionalClient.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Client found", optionalClient.get(), null));
        } else {
            ErrorDto error = new ErrorDto("Client not found", "No client exists with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Client not found", null, error));
        }
    }

    @PutMapping(value = "/client/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Client_Dto>> updateClient(
            @PathVariable String id,
            @RequestPart("dto") String dtoJson) {

        try {
            Client_Dto dto = objectMapper.readValue(dtoJson, Client_Dto.class);
            Optional<Client_Dto> updatedClient = service.updateClient(id, dto);

            if (updatedClient.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Client updated successfully", updatedClient.get(), null));
            } else {
                ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.NOT_FOUND), "No client exists with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Update failed", null, error));
            }
        } catch (JsonProcessingException e) {
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.BAD_REQUEST), "Invalid JSON: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Invalid JSON Format", null, error));
        } catch (Exception e) {
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Unexpected Error", null, error));
        }
    }

    @PutMapping(value = "/clients/updateDocuments/{clientId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<ClientDocument>>> updateOrAddDocuments(
            @PathVariable String clientId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(value = "deleteDocIds", required = false) List<Long> deleteDocIds) {

        try {
            if (deleteDocIds != null && !deleteDocIds.isEmpty()) {
                service.deleteDocumentsByIdsAndClient(deleteDocIds, clientId);
            }

            List<ClientDocument> updatedDocs = service.updateDocuments(clientId, files);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Documents updated, added and deleted as requested", updatedDocs, null));

        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
                // Client disconnected during upload — safe to ignore
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.BAD_REQUEST),
                    "File handling error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Invalid file data", null, error));

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
                // Same safeguard for async/stream close
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                    "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error updating documents", null, error));
        }
    }



    @GetMapping("/client/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        logger.info("Downloading document with ID: {}", documentId);

        Optional<ClientDocument> docOpt = clientDocumentRepository.findById(documentId);
        if (docOpt.isEmpty()) {
            logger.warn("Document with ID {} not found", documentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ClientDocument doc = docOpt.get();

        try {
            // Serve directly from DB
            ByteArrayResource resource = new ByteArrayResource(doc.getData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(doc.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getFileName() + "\"")
                    .contentLength(doc.getSize())
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error downloading document '{}': {}", doc.getFileName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @DeleteMapping("/client/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable String id) {
        service.deleteClient(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client deleted successfully", null, null));
    }
}
