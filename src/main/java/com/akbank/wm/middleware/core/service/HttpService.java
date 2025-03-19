package com.akbank.wm.middleware.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.akbank.wm.middleware.core.dto.HttpCallResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Service
public class HttpService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> sendFormData(String url, HttpMethod httpMethod, HttpHeaders headers,
            Map<String, String> data, String multipartFileKey, File file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        // Add form fields
        data.forEach(bodyBuilder::part);

        // Add file if provided
        if (file != null && file.exists()) {
            bodyBuilder.part(multipartFileKey, new FileSystemResource(file));
        }

        MultiValueMap<String, HttpEntity<?>> body = bodyBuilder.build();

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, httpMethod, requestEntity, String.class);
    }

    public ResponseEntity<String> makeFormUrlEncodedCall(String url, HttpMethod method,
            MultiValueMap<String, String> formData) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        return restTemplate.exchange(url, method, requestEntity, String.class);
    }

    public void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpCallResponseDTO makeHttpCall(String urlString, String method, String requestBody,
            Map<String, Object> headerList) {
        disableSslVerification();
        HttpCallResponseDTO responseMap = new HttpCallResponseDTO(0, null, null);

        HttpURLConnection con = null;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();

            con.setConnectTimeout(10000); // wait 10 sec to create connection

            con.setReadTimeout(600000); // wait 10 minutes to get response

            // Set request method
            con.setRequestMethod(method);

            // Set headers if present
            if (headerList != null) {
                for (Map.Entry<String, Object> entry : headerList.entrySet()) {
                    con.setRequestProperty(entry.getKey(), entry.getValue().toString());
                }
            }

            // Set request body if method is POST or PUT
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                con.setDoOutput(true);

                // Send request
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                } catch (Exception e) {
                    responseMap.setStatusCode(con.getResponseCode());
                    responseMap.setResponseBody((new ObjectMapper()).createObjectNode().put("message", e.toString()));
                    return responseMap;
                }
            }

            // Read the response
            int responseCode = con.getResponseCode();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? con.getInputStream() : con.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                responseMap.setStatusCode(responseCode);
                try {
                    responseMap.setResponseBody((new ObjectMapper()).readTree(response.toString()));
                } catch (Exception e) {
                    responseMap.setResponseBody(
                            (new ObjectMapper()).createObjectNode().put("message", response.toString()));
                }

                Map<String, List<String>> responseHeaderList = new HashMap<>(con.getHeaderFields());
                responseHeaderList.remove(null);
                responseMap.setHeaderList(responseHeaderList);

            }

        } catch (Exception e) {
            responseMap.setStatusCode(500);
            responseMap.setResponseBody((new ObjectMapper()).createObjectNode().put("error", e.toString()));
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return responseMap;
    }

}
