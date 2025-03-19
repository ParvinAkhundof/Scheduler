package com.akbank.wm.middleware.core.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.akbank.wm.middleware.core.dto.HttpCallResponseDTO;
import com.akbank.wm.middleware.core.util.GlobalVariables;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class ApimService {

    @Autowired   
    private HttpService httpService;

    public String getAcessToken(String clientId, String clientSecret) {

        String token = null;
        Map<String, String> parametersObject = GlobalVariables.getSystemVariables();
        String url = parametersObject.get("APIM_AUTH_URL");
        HttpMethod method = HttpMethod.POST; // Change this to PUT, DELETE, etc., as needed

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", parametersObject.get("grant_type_oauth2"));
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("scope", parametersObject.get("scope_oauth2"));

        try {
            ResponseEntity<String> response = httpService.makeFormUrlEncodedCall(url, method, formData);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            token = root.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parse the JSON response

        return token;

    }

    public HttpCallResponseDTO serviceCall(Map<String, Object> headerList, String acessToken,
            String subscriptionKey, String url, String method, String requestBody) {

        try {
            headerList.put("Content-Type", "application/json");
            headerList.put("Accept", "*/*");
            headerList.put("Authorization-Apim", acessToken);
            headerList.put("Ocp-Apim-Subscription-Key", subscriptionKey);

            return httpService.makeHttpCall(url, method, requestBody, headerList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to execute service call: " + e.getMessage(), e);
        }
    }
}
