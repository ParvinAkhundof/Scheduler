package com.akbank.wm.middleware.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ConverterService {

    private Map<String, String> ebcdicMap;
    private Map<String, String> asciiMap;

    /**
     * Constructor to load the EBCDIC and ASCII maps from JSON files When the
     * service is initialized.
     */
    public ConverterService() throws IOException {

        ebcdicMap = loadMap("/static/EBCDICMap.json");
        asciiMap = loadMap("/static/ASCIIMap.json");

    }

    /**
     * Loads a character map (EBCDIC or ASCII) from a JSON file.
     * 
     * @param jsonFilePath The path to the JSON file containing the map.
     * @return A Map<String, String> with hex values and corresponding characters.
     * @throws IOException if the file cannot be loaded or read.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> loadMap(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(jsonFilePath);

        InputStream inputStream = resource.getInputStream();

        // Read the input stream with the correct charset (UTF-8)
        String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        byte[] jsonBytes = jsonContent.getBytes(StandardCharsets.UTF_8);

        // Deserialize the JSON content
        String utf8Json = new String(jsonBytes, StandardCharsets.UTF_8);
        return objectMapper.readValue(utf8Json, Map.class);

    }

    /**
     * Converts an EBCDIC-encoded hex string to its ASCII representation using
     * predefined maps.
     * 
     * @param ebcdicInput EBCDIC-encoded hex string.
     * @return ASCII-encoded hex string with spaces separating each code.
     * @throws IOException if an error occurs during conversion.
     */
    public String convertEbcdicToAscii(String ebcdicInput) throws IOException {
        StringBuilder asciiOutput = new StringBuilder();

        // Split input into individual hex values
        String[] hexValues = ebcdicInput.split("\\s+");

        for (String hexValue : hexValues) {

            // Ensure hexValue is in the format "0xXX"
            String hexKey = "0x" + hexValue.toUpperCase();

            // Use EBCDIC map to get the corresponding string value
            String stringValue = ebcdicMap.getOrDefault(hexKey, "?");

            // Use ASCII map to convert string value to ASCII representation
            String asciiChar = "";
            for (Map.Entry<String, String> entry : asciiMap.entrySet()) {
                if (entry.getValue().equals(stringValue)) {

                    // Remove "0x" prefix for clean output
                    asciiChar = entry.getKey().substring(2);
                    break;
                }
            }

            // Append each ASCII character to the output string with space
            asciiOutput.append(asciiChar).append(" ");
        }

        // Remove trailing space
        return asciiOutput.toString().trim();
    }

    /**
     * Converts an ASCII-encoded hex string to its EBCDIC representation using
     * predefined maps.
     * 
     * @param asciiInput ASCII-encoded hex string (e.g., "61 62 63").
     * @return EBCDIC-encoded hex string with spaces separating each code.
     * @throws IOException if an error occurs during conversion.
     */
    public String convertAsciiToEbcdic(String asciiInput) throws IOException {
        StringBuilder ebcdicOutput = new StringBuilder();

        // Split input into individual hex values
        String[] hexValues = asciiInput.split("\\s+");

        for (String hexValue : hexValues) {
            // Ensure hexValue is in the format "0xXX"
            String hexKey = "0x" + hexValue.toUpperCase();

            // Use ASCII map to get the corresponding string value
            String stringValue = asciiMap.getOrDefault(hexKey, "?");

            // Use EBCDIC map to convert string value to EBCDIC representation
            String ebcdicChar = "";
            for (Map.Entry<String, String> entry : ebcdicMap.entrySet()) {
                if (entry.getValue().equals(stringValue)) {
                    ebcdicChar = entry.getKey().substring(2);
                    break;
                }
            }

            // Append each EBCDIC character to the output string with space
            ebcdicOutput.append(ebcdicChar).append(" ");
        }

        // Remove trailing space
        return ebcdicOutput.toString().trim();
    }

}
