package com.frpr.service;

import com.frpr.model.FacilityRegistry;
import com.frpr.model.ProviderRegistry;
import com.frpr.pojo.BulkUploadRequest;
import com.frpr.repo.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProviderRegistryService {

    @Autowired
    ProviderRepository providerRepository;
    @Autowired
    RestTemplate restTemplate;

    @Value("${fr.url}")
    private String frBaseUrl;

    public String saveAll(List<BulkUploadRequest> bulkUploadRequests) {

        try {
            List<ProviderRegistry> providerRegistries = new ArrayList<>();
            for (BulkUploadRequest bulkUploadRequest : bulkUploadRequests) {
                ProviderRegistry providerRegistry = mapData(bulkUploadRequest);

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
                providerRegistry.setLicense_status(checkIfExpired(providerRegistry.getLicenseExpiryDate()));
                providerRepository.save(providerRegistry);
                providerRegistries.add(providerRegistry);
            }
           // providerRepository.saveAll(providerRegistries);
        } catch (Exception ioe) {
            throw new RuntimeException(ioe);
        }
        return "";
    }



    public List<FacilityRegistry> getFR(ProviderRegistry registry) {
        String url = frBaseUrl + "/api/v1/facility-registry/byids";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<ProviderRegistry> entity = new HttpEntity<ProviderRegistry>(registry, headers);

        return Objects.requireNonNull(restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<List<FacilityRegistry>>() {
        }).getBody());

    }


    public String checkIfExpired(Date exp){

        Date now = new Date();

        if(exp == null || now.after(exp)){
            return "Expired";
        }
        return "Valid";
    }

    private ProviderRegistry mapData(BulkUploadRequest request) {
        ProviderRegistry providerRegistry = new ProviderRegistry();
        providerRegistry.setDocumentNumber(request.getDocumentNumber());
        providerRegistry.setDocumentType(request.getDocumentType());
        providerRegistry.setApplicationNumber(request.getApplicationNumber());
        providerRegistry.setSurName(request.getSurName());
        providerRegistry.setPostNames(request.getPostNames());
        providerRegistry.setMaritalStatus(request.getMaritalStatus());
        providerRegistry.setSex(request.getSex());
        providerRegistry.setNationality(request.getNationality());
        providerRegistry.setDomicileCountry(request.getDomicileCountry());
        providerRegistry.setDomicileProvince(request.getDomicileProvince());
        providerRegistry.setDomicileDistrict(request.getDomicileDistrict());
        providerRegistry.setDomicileCell(request.getDomicileCell());
        providerRegistry.setDomicileSector(request.getDomicileSector());
        providerRegistry.setDomicileVillage(request.getDomicileVillage());
        providerRegistry.setLicenseNumber(request.getLicenseNumber());
        providerRegistry.setLicenseExpiryDate(parseDate(request.getLicenseExpiryDate()));
        providerRegistry.setLicense_status(request.getLicense_status());
        providerRegistry.setDateOfBirth(request.getDateOfBirth());
        providerRegistry.setEmail(request.getEmail());
        providerRegistry.setPhoneNumber(request.getPhoneNumber());
        providerRegistry.setQualification(request.getQualification());
        providerRegistry.setStatus(request.getStatus().toUpperCase());
        if(request.getFacilityId() == null){
            throw new RuntimeException("Facility Id not present for the record "+ request.getEmail());
        }
        providerRegistry.setFacilityId(Collections.singletonList(request.getFacilityId()));
        List<FacilityRegistry> facilityRegistries = getFR(providerRegistry);
        providerRegistry.setFacilityId(facilityRegistries.stream().map(FacilityRegistry::get_id).collect(Collectors.toList()));
        providerRegistry.setFacilities(facilityRegistries);

        return providerRegistry;
    }

    private Date parseDate(String dateString) {
        if(dateString == null || dateString.isEmpty()){
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return  formatter.parse(dateString);
        } catch (ParseException e) {
             formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                return  formatter.parse(dateString);
            } catch (ParseException ex) {
                ex.printStackTrace();
                //throw new RuntimeException(ex);
            }
        }
        return null;
    }
}
