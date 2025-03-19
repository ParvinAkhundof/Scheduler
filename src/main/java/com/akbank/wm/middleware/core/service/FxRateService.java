package com.akbank.wm.middleware.core.service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.akbank.wm.middleware.core.dto.FxRateResponseDTO;
import com.akbank.wm.middleware.core.entity.FxRate;
import com.akbank.wm.middleware.core.repository.FxRateRepository;
import com.akbank.wm.middleware.core.util.GlobalVariables;


@Service
public class FxRateService {

    @Autowired 
    private FxRateRepository fxRateRepository;

    @Autowired 
    private EmailService emailService;

    public void updateStatusToPForTodayRecords(LocalDate date) {
        fxRateRepository.updateExistRates(date);
    }

    public void deleteExistRatesForDate(LocalDate date) {
        fxRateRepository.deleteExistRates(date);
    }

    public void insertData(FxRate fxRate) {
        fxRateRepository.save(fxRate);
    }

    public void addRates() {

        RestTemplate restTemplate = new RestTemplate();

        String url = GlobalVariables.getSystemVariables().get("ECB_Rate_URL").toString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String xmlResponse = response.getBody();

        try {
            // Create a DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Create an InputSource from the XML string
            InputSource inputSource = new InputSource(new StringReader(xmlResponse));

            // Parse the InputSource to a Document object
            Document doc = builder.parse(inputSource);

            // Get the date from the Cube element
            Element cubeElement = (Element) doc.getElementsByTagName("Cube").item(1);
            String date = cubeElement.getAttribute("time");

            // Get all Cube elements containing currency-rate pairs
            NodeList cubeList = doc.getElementsByTagName("Cube");

            // Iterate over Cube elements to get currency-rate pairs

            for (int i = 0; i < cubeList.getLength(); i++) {
                Element cube = (Element) cubeList.item(i);
                if (cube.hasAttribute("currency") && cube.hasAttribute("rate")) {
                    String currency = cube.getAttribute("currency");
                    Double rate = Double.parseDouble(cube.getAttribute("rate"));

                    FxRate fxRate = new FxRate();
                    fxRate.setDataSource("ECB");
                    fxRate.setRateType("ACCOUNTING");
                    fxRate.setRateDate(LocalDate.parse(date));
                    fxRate.setCcy("EUR");
                    fxRate.setCcyCtr(currency);
                    fxRate.setRate(rate);
                    fxRate.setBuyRate(rate);
                    fxRate.setSellRate(rate);
                    this.insertData(fxRate);

                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            emailService.errorNotificationEmail("addRates", e.toString());
        }

    }

    public Double convertRate(String sourceCcy, String targetCcy, LocalDate rateDate) {
        FxRate sourceRate = fxRateRepository.findLastRateofCurrency("A", sourceCcy, rateDate);
        FxRate targetRate = fxRateRepository.findLastRateofCurrency("A", targetCcy, rateDate);
        try {
            if (sourceCcy.equals(targetCcy)) {
                return 1.0;
            } else if (sourceCcy.equals("EUR")) {
                return targetRate.getRate();
            } else if (targetCcy.equals("EUR")) {
                return 1.0 / sourceRate.getRate();
            } else {
                return targetRate.getRate() / sourceRate.getRate();
            }
        } catch (Exception e) {
            return 0.0;
        }

    }

    // Method to return specific rates based on filters using repository
    public List<FxRateResponseDTO> getRates(LocalDate rateDate, String rateType, List<String> ccyList) {

        List<FxRateResponseDTO> fxRateDTOs = null;
        try {

            List<FxRate> fxRates = fxRateRepository.findByRecordStatusAndRateDateAndRateTypeAndCcyCtrIn("A", rateDate,
                    rateType, ccyList);

            // Convert FxRate entities to FxRateDTO objects
            fxRateDTOs = fxRates.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {

            emailService.errorNotificationEmail("getRates", e.toString());
        }

        return fxRateDTOs;
    }

    private FxRateResponseDTO convertToDto(FxRate fxRate) {
        FxRateResponseDTO fxRateDTO = new FxRateResponseDTO();
        fxRateDTO.setRateDate(fxRate.getRateDate());
        fxRateDTO.setRateType(fxRate.getRateType());
        fxRateDTO.setCcy(fxRate.getCcyCtr()); // Set Ccy as CcyCtr
        fxRateDTO.setRate(fxRate.getRate());
        fxRateDTO.setBuyRate(fxRate.getBuyRate());
        fxRateDTO.setSellRate(fxRate.getSellRate());
        return fxRateDTO;
    }

}
