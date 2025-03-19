package com.akbank.wm.middleware.core.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.akbank.wm.middleware.core.entity.ServiceErrorLog;
import com.akbank.wm.middleware.core.repository.ServiceErrorLogRepository;
import com.akbank.wm.middleware.core.util.GlobalVariables;

import org.w3c.dom.*;

import javax.xml.xpath.*;
import java.io.*;

@Service
public class GeneralService {

    @Autowired 
    private ServiceErrorLogRepository serviceErrorLogRepository;

    @Autowired 
    private EmailService emailService;

    @Autowired 
    private ApimService apimService;

    @Autowired 
    private HttpService httpService;

    public void saveServiceErrorLog(String resource, String serviceName, LocalDate referenceDate, String referenceId,
            int errorCode, String errorMessage, Exception exceptionObject, String errorServerity,
            boolean resolvedStatus, String additionalInfo) {
        ServiceErrorLog serviceErrorLog = new ServiceErrorLog();

        serviceErrorLog.setResource(resource);
        serviceErrorLog.setReferenceDate(referenceDate);
        serviceErrorLog.setReferenceId(referenceId);
        serviceErrorLog.setServiceName(serviceName);
        serviceErrorLog.setErrorCode(500);
        serviceErrorLog.setErrorMessage(errorMessage);
        serviceErrorLog.setStackTrace(getStackTraceAsString(exceptionObject));
        serviceErrorLog.setErrorSeverity(errorServerity);
        serviceErrorLog.setResolvedStatus(resolvedStatus);
        serviceErrorLog.setAdditionalInfo(additionalInfo);
        serviceErrorLogRepository.save(serviceErrorLog);

        emailService.errorNotificationEmail(resource + " - " + serviceName,
                "AdditionalInfo: " + additionalInfo + ", Error: " + getStackTraceAsString(exceptionObject));
    }

    public String getStackTraceAsString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }

    /**
     * Takes a request containing client details and generates a CONCAT identifier.
     * 
     * @param client The client request data
     * @return The generated CONCAT identifier.
     * @throws IllegalArgumentException if no valid identifier is found for the
     *                                  country
     */
    

    /**
     * Transliterates a given string by replacing special characters with their
     * mapped equivalents
     * 
     * @param inputString        The original string to be transliterated
     * @param transliterationMap A mapping of Unicode keys to transliterate
     *                           characters
     * @return The transliterated string in uppercase
     */
    public String transliterateString(String inputString) {

        // #######################################################
        // Load transliteration mapping from JSON file
        Map<String, String> transliterationMap;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = new ClassPathResource("/static/transliterationMap.json").getInputStream();
            transliterationMap = objectMapper.readValue(inputStream, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load transliteration map", e);
        }

        StringBuilder transliteratedString = new StringBuilder();
        for (char c : inputString.toCharArray()) {
            // Convert character to Unicode code point
            String unicodeKey = "U+" + String.format("%04X", (int) c);

            // Handle international letters (A-Z, a-z) because they don't exist in map
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                transliteratedString.append(c); // Pass through unchanged
                continue;
            }

            String transliteratedChar = transliterationMap.get(unicodeKey);
            if (transliteratedChar != null) {
                transliteratedString.append(transliteratedChar);
            }

        }
        return transliteratedString.toString().toUpperCase();
    }

    public ResponseEntity<String> proceedPaymentOutgoing(String payload, String externalId, String channel,
            String originator) throws IOException {

        Map<String, String> data = new HashMap<>();

        data.put("channel", channel);
        data.put("originator", originator);
        data.put("filename", externalId);
        File sp2File = createSp2File(payload);
        Map<String, String> parametersObject = GlobalVariables.getSystemVariables();

        String auth = apimService.getAcessToken(parametersObject.get("MAMBUclientIdAPIM").toString(),
                parametersObject.get("MAMBUclientSecretAPIM").toString());

        // Return XML response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Ocp-Apim-Subscription-Key", parametersObject.get("APIMsubscriptionKey").toString());
        headers.add("Authorization-Apim", auth);

        return httpService.sendFormData("https://apimtest.akbank.de/int/geva/file-data/", HttpMethod.POST, headers,
                data, "data", sp2File);

    }

    public static File createSp2File(String externalId) throws IOException {
        File file = new File("externalId.sp2");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(externalId);
        }
        return file;
    }

    public String getElementValueFromXML(Document document, String xpathExpression, String xmlPrefix) throws Exception {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        // Define Namespace Context Inline
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if (xmlPrefix.equals(prefix)) {
                    return "http://tempuri.org/";
                }
                return XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null; // Not needed
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null; // Not needed
            }
        });

        XPathExpression expr = xpath.compile(xpathExpression);
        return expr.evaluate(document, XPathConstants.STRING).toString();
    }

}
