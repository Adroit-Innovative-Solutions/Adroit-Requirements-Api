package com.dataquadinc.controller;

import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.model.Client;
import com.dataquadinc.repository.ClientRepository;
import com.dataquadinc.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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

    private final Path UPLOAD_DIR = Paths.get("uploads");

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/client/addClient")
    public ResponseEntity<ApiResponse<Client_Dto>> createClient(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "supportingDocuments", required = false) List<MultipartFile> files) {

        try {
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

    @PutMapping(value = "/client/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Client_Dto>> updateClient(
            @PathVariable String id,
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "supportingDocuments", required = false) List<MultipartFile> files) {

        try {
            Client_Dto dto = objectMapper.readValue(dtoJson, Client_Dto.class);
            Optional<Client_Dto> updatedClient = service.updateClient(id, dto, files);

            if (updatedClient.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Client updated successfully", updatedClient.get(), null));
            } else {
                ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.NOT_FOUND), "No client exists with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Update failed", null, error));
            }
        } catch (JsonProcessingException e) {
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.BAD_REQUEST), "Error in JSON structure: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Invalid JSON Format", null, error));
        } catch (IOException e) {
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.BAD_REQUEST), "Could not parse client data." + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "JSON Parsing Error", null, error));
        } catch (Exception e) {
            ErrorDto error = new ErrorDto(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Unexpected Error", null, error));
        }
    }

    @GetMapping("/ClientsDocuments/downloadAll/{id}")
    public ResponseEntity<Resource> downloadAllSupportingDocuments(@PathVariable String id) {
        Optional<Client> clientOptional = repo.findById(id);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            List<String> fileNames = client.getSupportingDocuments();

            if (fileNames == null || fileNames.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

                for (String fileName : fileNames) {
                    Path filePath = Paths.get("uploads", fileName);
                    if (Files.exists(filePath)) {
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));
                        Files.copy(filePath, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
                zipOutputStream.finish();

                ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Client_" + id + "_Documents.zip\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(resource.contentLength())
                        .body(resource);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/client/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable String id) {
        service.deleteClient(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client deleted successfully", null, null));
    }
}
