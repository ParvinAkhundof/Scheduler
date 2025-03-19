package com.company.wm.middleware.core.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.wm.middleware.core.entity.CmSystem;
import com.company.wm.middleware.core.repository.CmSystemRepository;

import java.util.Map;

@Service
public class CmSystemService {

    @Autowired 
    private CmSystemRepository cmSystemRepository;

    // Method to return specific rates based on filters using repository
    public Map<String, String> getParameters() {

        try {
            List<CmSystem> parameters = cmSystemRepository.findByRecordStatus("A");
            Map<String, String> parametersObject = new HashMap<>();

            for (int i = 0; i < parameters.size(); i++) {
                parametersObject.put(parameters.get(i).getParameter(), parameters.get(i).getParameterValue());

            }

            return parametersObject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve parameters: " + e.getMessage(), e);
        }

    }

}
