package com.akbank.wm.middleware.core.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FxRateResponseDTO {
    private LocalDate rateDate;

    public LocalDate getRateDate() {
        return rateDate;
    }

    private String rateType;
    private String ccy;
    private Double rate;
    private Double buyRate;
    private Double sellRate;
    private List<String> ccyList;

}