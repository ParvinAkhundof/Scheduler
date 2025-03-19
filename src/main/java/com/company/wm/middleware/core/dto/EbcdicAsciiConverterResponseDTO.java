package com.company.wm.middleware.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbcdicAsciiConverterResponseDTO {
    private String message;        // "success" or "fail"
    private String response;  // Converted result or error message
}
