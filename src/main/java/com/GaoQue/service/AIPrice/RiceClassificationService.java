package com.GaoQue.service.AIPrice;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class RiceClassificationService {

    @Autowired
    private RestTemplate restTemplate;



    private String flaskApiUrl = "http://localhost:11223"; // Địa chỉ API Flask

    public String classifyRice(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource imageResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(imageResource, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskApiUrl + "/predict", requestEntity, Map.class);

        return response.getBody().get("prediction").toString();
    }
}
