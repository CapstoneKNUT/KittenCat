package org.zerock.b01.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zerock.b01.dto.PlaceSearchDTO;
import org.zerock.b01.dto.TransTimeDTO;

@Service
public class ApiServiceImpl implements ApiService {

    @Override
    public String callExternalApi(String url, PlaceSearchDTO placeSearchDTO) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlaceSearchDTO> requestEntity = new HttpEntity<>(placeSearchDTO, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("API Response: " + response.getBody());
        return response.getBody();
    }

    @Override
    public String callTransportApi(String url, TransTimeDTO transTimeDTO) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransTimeDTO> requestEntity = new HttpEntity<>(transTimeDTO, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("API Response: " + response.getBody());
        return response.getBody();
    }
}
