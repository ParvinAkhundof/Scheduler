package com.akbank.wm.middleware.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbcdicAsciiConverterRequestDTO {
    private String type;        // Either "AsciiToEbcdic" or "EbcdicToAscii"
    private String hexMessage;  // The input string (in hex format)
}
