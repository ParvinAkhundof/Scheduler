package com.company.wm.middleware.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class FxRateRequestDTO {

    @NotNull
    @JsonProperty("RateDate")
    private LocalDate rateDate;

    @NotBlank
    @JsonProperty("RateType")
    private String rateType;

    @NotNull
    @Size(min = 1)
    @JsonProperty("CcyList")
    private List<String> ccyList;

}
