package com.akbank.wm.middleware.core.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HttpCallResponseDTO {

    private int statusCode;
    private JsonNode responseBody;
    private Map<String, List<String>> headerList;

    public HttpCallResponseDTO(int newStatusCode, JsonNode newResponseBody, Map<String, List<String>> newHeaderList) {
        statusCode = newStatusCode;
        responseBody = newResponseBody;
        headerList = newHeaderList;
    }

}