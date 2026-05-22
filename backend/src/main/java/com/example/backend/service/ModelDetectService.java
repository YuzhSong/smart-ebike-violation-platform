package com.example.backend.service;

import com.example.backend.dto.ModelDetectResultDto;
import com.example.backend.exception.BizException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ModelDetectService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final String aiServiceUrl;

    public ModelDetectService(
            ObjectMapper objectMapper,
            @Value("${app.ai-service-url:http://127.0.0.1:8000}") String aiServiceUrl
    ) {
        this.objectMapper = objectMapper;
        this.aiServiceUrl = trimTrailingSlash(aiServiceUrl);
    }

    /**
     * Call the AI service image endpoint and convert the first detection into the
     * backend event fields currently stored by DeviceReportService.
     */
    public DetectResult detect(MultipartFile image) {
        try {
            String rawResponse = postFile(endpoint("/detect/image"), image);
            if (rawResponse == null || rawResponse.isBlank()) {
                throw new BizException(500, "AI service returned empty result");
            }
            return parse(rawResponse);
        } catch (IOException e) {
            throw new BizException(500, "failed to read upload image");
        } catch (RestClientException e) {
            throw new BizException(500, "AI service unavailable");
        }
    }

    public JsonNode detectVideo(MultipartFile video) {
        try {
            String rawResponse = postFile(endpoint("/detect/video"), video);
            if (rawResponse == null || rawResponse.isBlank()) {
                throw new BizException(500, "AI service returned empty result");
            }
            return objectMapper.readTree(rawResponse);
        } catch (IOException e) {
            throw new BizException(500, "failed to read upload video");
        } catch (RestClientException e) {
            throw new BizException(500, "AI service unavailable");
        }
    }

    private String postFile(String url, MultipartFile file) throws IOException {
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return restTemplate.postForObject(
                url,
                new HttpEntity<>(body, headers),
                String.class
        );
    }

    private String endpoint(String path) {
        return aiServiceUrl + path;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://127.0.0.1:8000";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    // The current persistence flow stores one violation event per device report, so
    // this adapter validates and returns the first AI detection.
    private DetectResult parse(String rawResponse) throws IOException {
        JsonNode root = objectMapper.readTree(rawResponse);
        if (!root.path("success").asBoolean(false)) {
            throw new BizException(500, "AI service returned unsuccessful result");
        }

        JsonNode detections = root.path("detections");
        if (!detections.isArray() || detections.isEmpty()) {
            throw new BizException(500, "AI service returned no detection result");
        }

        JsonNode first = detections.get(0);
        String label = first.path("class_name").asText(null);
        if (label == null || label.isBlank()) {
            throw new BizException(500, "AI service result missing class_name");
        }

        BigDecimal confidence = first.hasNonNull("confidence")
                ? first.get("confidence").decimalValue()
                : null;
        List<Integer> bbox = new ArrayList<>();
        JsonNode bboxNode = first.path("bbox");
        if (bboxNode.isArray()) {
            bboxNode.forEach(item -> bbox.add(item.asInt()));
        }

        return new DetectResult(new ModelDetectResultDto(label, confidence, bbox), rawResponse);
    }

    public record DetectResult(ModelDetectResultDto firstResult, String rawResponse) {
    }
}
