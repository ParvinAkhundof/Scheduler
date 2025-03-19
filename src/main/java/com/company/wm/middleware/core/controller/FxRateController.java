package com.company.wm.middleware.core.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.wm.middleware.core.dto.FxRateRequestDTO;
import com.company.wm.middleware.core.dto.FxRateResponseDTO;
import com.company.wm.middleware.core.service.FxRateService;

@RestController
@RequestMapping("/fx-rate")
public class FxRateController {

    @Autowired 
    private FxRateService fxRateService;

    @PostMapping("/add-rates")
    public void addRates() {
        fxRateService.addRates();

    }

    @PostMapping("/get-rates")
    public ResponseEntity<List<FxRateResponseDTO>> getRates(@Valid @RequestBody FxRateRequestDTO requestBody) {

        return ResponseEntity.ok(
                fxRateService.getRates(requestBody.getRateDate(), requestBody.getRateType(), requestBody.getCcyList()));
    }

}
