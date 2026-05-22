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
    private final String modelServiceUrl;

    public ModelDetectService(
            ObjectMapper objectMapper,
            @Value("${app.model-service-url:http://localhost:8000/detect}") String modelServiceUrl
    ) {
        this.objectMapper = objectMapper;
        this.modelServiceUrl = modelServiceUrl;
    }

    /**
     * 调用模型服务 /detect，并把模型返回的 JSON 转换成后端可落库的识别结果。
     *
     * @param image 待识别图片
     * @return 第一条识别结果和模型原始响应
     * @throws BizException 图片读取失败、模型服务不可用或返回结果不符合契约时抛出
     */
    public DetectResult detect(MultipartFile image) {
        try {
            ByteArrayResource fileResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename() == null ? "capture.jpg" : image.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            String rawResponse = restTemplate.postForObject(
                    modelServiceUrl,
                    new HttpEntity<>(body, headers),
                    String.class
            );
            if (rawResponse == null || rawResponse.isBlank()) {
                throw new BizException(500, "model service returned empty result");
            }
            return parse(rawResponse);
        } catch (IOException e) {
            throw new BizException(500, "failed to read upload image");
        } catch (RestClientException e) {
            throw new BizException(500, "model service unavailable");
        }
    }

    // 中文注释：当前后端只取第一条检测结果入库，因此这里校验 result[0] 的必需字段。
    private DetectResult parse(String rawResponse) throws IOException {
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode resultArray = root.path("result");
        if (!resultArray.isArray() || resultArray.isEmpty()) {
            throw new BizException(500, "model service returned no detection result");
        }

        JsonNode first = resultArray.get(0);
        String label = first.path("label").asText(null);
        if (label == null || label.isBlank()) {
            throw new BizException(500, "model service result missing label");
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
