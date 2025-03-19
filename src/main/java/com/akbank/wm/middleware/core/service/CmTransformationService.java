package com.akbank.wm.middleware.core.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akbank.wm.middleware.core.entity.CmTransformation;
import com.akbank.wm.middleware.core.repository.CmTransformationRepository;


import java.util.Map;

@Service
public class CmTransformationService {

    @Autowired 
    private CmTransformationRepository cmTransformationRepository;

    public List<CmTransformation> getTransformationObject(String sourceSystem, String destinationSystem) {

        try {
            List<CmTransformation> transformationList = cmTransformationRepository
                    .findByRecordStatusAndSourceSystemAndDestinationSystem("A", sourceSystem, destinationSystem);
            Map<String, String> transformationObject = new HashMap<>();

            for (int i = 0; i < transformationList.size(); i++) {
                transformationObject.put(transformationList.get(i).getSourceValue(),
                        transformationList.get(i).getDestinationValue());

            }

            return transformationList;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve transformation data: " + e.getMessage(), e);
        }

    }

    public String findDestinationValue(List<CmTransformation> transformationList, String sourceValue,
            String description) {
        try {
            for (CmTransformation transformationObject : transformationList) {
                if ((transformationObject.getSourceValue().equals(sourceValue) && description == null) ||
                        (transformationObject.getSourceValue().equals(sourceValue)
                                && transformationObject.getDescription().equals(description))) {

                    return transformationObject.getDestinationValue();
                }
            }

            return null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to find destination value: " + e.getMessage(), e);
        }

    }

}
