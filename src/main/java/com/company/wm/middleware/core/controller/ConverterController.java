package com.company.wm.middleware.core.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.wm.middleware.core.dto.EbcdicAsciiConverterRequestDTO;
import com.company.wm.middleware.core.dto.EbcdicAsciiConverterResponseDTO;
import com.company.wm.middleware.core.service.ConverterService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for handling EBCDIC-to-ASCII and ASCII-to-EBCDIC conversion
 * requests.
 * This controller is active only in the "dev" and "test" profiles.
 */
@RestController
@RequestMapping("/converter")
public class ConverterController {

    @Autowired 
    private ConverterService converterService;

    /**
     * POST endpoint for converting a message between EBCDIC and ASCII formats.
     * 
     * @param request DTO containing the type of conversion ("EbcdicToAscii" or
     *                "AsciiToEbcdic")
     *                and the hex-encoded message to be converted.
     * @return ResponseEntity containing a success or error response with the
     *         converted message or error details.
     */
    @PostMapping("/convertEbcdicAscii")
    public ResponseEntity<EbcdicAsciiConverterResponseDTO> convertEbcdicAscii(
            @Valid @RequestBody EbcdicAsciiConverterRequestDTO request) {

        // Extract the conversion type and hex message from the request DTO
        String type = request.getType();
        String hexMessage = request.getHexMessage();

        try {
            String output;

            if ("EbcdicToAscii".equalsIgnoreCase(type)) {
                output = converterService.convertEbcdicToAscii(hexMessage);
            } else if ("AsciiToEbcdic".equalsIgnoreCase(type)) {
                output = converterService.convertAsciiToEbcdic(hexMessage);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new EbcdicAsciiConverterResponseDTO("fail",
                                "Invalid type. Use 'EbcdicToAscii' or 'AsciiToEbcdic'"));
            }

            return ResponseEntity.ok(new EbcdicAsciiConverterResponseDTO("success", output));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EbcdicAsciiConverterResponseDTO("fail", "Error during conversion"));
        }
    }
}
