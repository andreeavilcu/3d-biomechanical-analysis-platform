package com.licenta.biomechanics_backend.service;

import com.licenta.biomechanics_backend.exception.PythonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class PythonProcessingService {

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Value("${python.service.timeout:30000}")
    private int timeout;

    private final RestTemplate restTemplate;

    public PythonProcessingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public Map<String, Object> processPointCloud(MultipartFile file) {
        log.info("Sending file to Python service: {} ({} bytes)",
                file.getOriginalFilename(), file.getSize());

        try {

            validateFile(file);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);


            String endpoint = pythonServiceUrl + "/process-scan";
            log.debug("Calling Python endpoint: {}", endpoint);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    endpoint,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new PythonProcessingException(
                        "Python service returned status: " + response.getStatusCode()
                );
            }

            Map<String, Object> result = response.getBody();

            if (result == null) {
                throw new PythonProcessingException("Python service returned null response");
            }

            if (result.containsKey("error")) {
                String error = (String) result.get("error");
                throw new PythonProcessingException("Python processing failed: " + error);
            }

            log.info("Successfully processed point cloud. Keypoints detected: {}",
                    result.containsKey("keypoints"));

            return result;

        } catch (IOException e) {
            log.error("Failed to read uploaded file", e);
            throw new PythonProcessingException("Failed to read uploaded file: " + e.getMessage());
        } catch (RestClientException e) {
            log.error("Failed to communicate with Python service", e);
            throw new PythonProcessingException(
                    "Python service is unavailable. Please ensure it's running on " + pythonServiceUrl
            );
        }
    }

    public boolean isHealthy() {
        try {
            String healthEndpoint = pythonServiceUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthEndpoint, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("Python service health check failed: {}", e.getMessage());
            return false;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new PythonProcessingException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".ply") && !filename.endsWith(".pcd"))) {
            throw new PythonProcessingException(
                    "Invalid file format. Only .ply and .pcd files are supported"
            );
        }


        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new PythonProcessingException(
                    "File too large. Maximum size is 50MB"
            );
        }
    }
}