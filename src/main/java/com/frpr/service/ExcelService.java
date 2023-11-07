package com.frpr.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.frpr.helper.ExcelHelper;
import com.frpr.model.ProviderRegistry;
import com.frpr.repo.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {
    @Autowired
    ProviderRepository providerRepository;

    public String save(MultipartFile file) {
        try {
            List<ProviderRegistry> providerRegistries = ExcelHelper.excelToProviderRegistry(file.getInputStream());
            for (ProviderRegistry providerRegistry : providerRegistries) {
                if (providerRegistry.getLicenseNumber() == null) {
                    return "Licence Number is null, Please update the details and submit again";
                } else {
                    List<ProviderRegistry> p = providerRepository.findAllByLicenseNumber(providerRegistry.getLicenseNumber());
                    if (!p.isEmpty()) {
                        return "Licence Number already exist: " + providerRegistry.getLicenseNumber();
                    }
                }
                if (providerRegistry.getDocumentNumber() != null) {
                    List<ProviderRegistry> p = providerRepository.findAllByDocumentNumber(providerRegistry.getDocumentNumber());
                    if (!p.isEmpty()) {
                        return "Document Number already exist: " + providerRegistry.getDocumentNumber();
                    }
                }
                providerRepository.save(providerRegistry);
            }
            //providerRepository.saveAll(providerRegistries);
        } catch (IOException ioe) {
            throw new RuntimeException("Fail to store data: " + ioe.getMessage());
        }
        return "";
    }
}
