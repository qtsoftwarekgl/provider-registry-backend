package com.frpr.controlller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.frpr.helper.ExcelHelper;
import com.frpr.model.Action;
import com.frpr.pojo.BulkUploadRequest;
import com.frpr.service.AuditService;
import com.frpr.service.ExcelService;
import com.frpr.service.ProviderRegistryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.frpr.model.FacilityRegistry;
import com.frpr.services.ProviderExpireDate;
import com.frpr.model.ProviderRegistry;
import com.frpr.repo.FacilityRepository;
import com.frpr.repo.ProviderRepository;
import com.frpr.response.CommonResponse;
import org.springframework.web.multipart.MultipartFile;

import static com.frpr.utils.Utility.setEndOfDay;

@RestController
@RequestMapping("/v1/provider-registry")
public class ProviderRegistryController {

    @Autowired
    ProviderRepository repository;


    ProviderExpireDate serve;

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${fr.url}")
    private String frBaseUrl;


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    ExcelService excelService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ProviderRegistryService providerRegistryService;

    private String getLoggedInUser() {
        try {
            return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }catch (Exception ignored){

        }
        return "";
    }

    @PostMapping
    public ResponseEntity<CommonResponse> save(@RequestBody ProviderRegistry providerRegistry, HttpServletRequest request) {
        //     user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if (isAlreadyExist(null, providerRegistry.getLicenseNumber(), providerRegistry.getDocumentNumber(), providerRegistry.getNin(), providerRegistry.getNid())) {
            return new ResponseEntity<CommonResponse>(new CommonResponse("error", "server_error.user_already_exist_with_licence_Number_or_Document_Number"), HttpStatus.ACCEPTED);
        }
        providerRegistry.setLicense_status(providerRegistryService.checkIfExpired(providerRegistry.getLicenseExpiryDate()));
        ProviderRegistry u = repository.save(providerRegistry);
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.ADD, providerRegistry, null, u, request, providerRegistry.getSurName());
        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(u), HttpStatus.ACCEPTED);
    }

    @PutMapping("/{prId}")
    public ResponseEntity<CommonResponse> update(@PathVariable String prId, @RequestBody ProviderRegistry providerRegistry, HttpServletRequest request) {

        try {
            ProviderRegistry pr = repository.findBy_id(prId);
            ProviderRegistry oldData = repository.findBy_id(prId);

            if (providerRegistry.getDateOfBirth() == null) {

                if(providerRegistry.getStatus() == null || (providerRegistry.getStatus().equalsIgnoreCase("INACTIVE") && StringUtils.isBlank(providerRegistry.getDeactivateReason()))){
                    return new ResponseEntity<CommonResponse>(new CommonResponse("error", "server_error.deactivate status should be provided").setData(providerRegistry), HttpStatus.ACCEPTED);
                }

                pr.setStatus(providerRegistry.getStatus());
                pr.setCreatedAt(oldData.getCreatedAt());
                pr.setDeactivateReason(providerRegistry.getDeactivateReason());
                pr = repository.save(pr);
                auditService.saveAuditLogBlocking(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.UPDATE, providerRegistry, oldData, pr, request, providerRegistry.getSurName());
                return new ResponseEntity<CommonResponse>(new CommonResponse("ok", "server_success.updated_facility").setData(pr), HttpStatus.ACCEPTED);
            } else {
                if (isAlreadyExist(prId, providerRegistry.getLicenseNumber(), providerRegistry.getDocumentNumber(), providerRegistry.getNin(), providerRegistry.getNid())) {
                    return new ResponseEntity<CommonResponse>(new CommonResponse("error", "server_error.user_already_exist_with_licence_Number_or_Document_Number"), HttpStatus.ACCEPTED);
                }
                providerRegistry.set_id(prId);
                providerRegistry.setCreatedAt(oldData.getCreatedAt());
                providerRegistry.setLicense_status(providerRegistryService.checkIfExpired(providerRegistry.getLicenseExpiryDate()));
                pr = repository.save(providerRegistry);
                auditService.saveAuditLogBlocking(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.UPDATE, providerRegistry, oldData, pr, request, providerRegistry.getSurName());
                return new ResponseEntity<CommonResponse>(new CommonResponse("ok", "server_success.updated_users").setData(pr), HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            return new ResponseEntity<CommonResponse>(new CommonResponse("error", "server_error.update_error"), HttpStatus.ACCEPTED);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse> getOnce(@PathVariable String id, HttpServletRequest request) {
        Optional<ProviderRegistry> u = repository.findById(id);
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.GET, id, u.orElse(null), request, u.get().getSurName());
        ProviderRegistry registry = new ProviderRegistry();
        registry.setFacilityId(u.get().getFacilityId());
        registry.setStatus(registry.getStatus().toUpperCase());
        u.get().setFacilities(providerRegistryService.getFR(registry));
        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(u.get()), HttpStatus.ACCEPTED);
    }


    @GetMapping
    public ResponseEntity<CommonResponse> getAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit, @RequestParam(required = false) String licenseNumber,
                                                 @RequestParam(required = false) String facilityName, @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String license_status,
                                                 @RequestParam(required = false) String postNames,
                                                 @RequestParam(required = false) String surName,
                                                 @RequestParam(required = false) String documentNumber,
                                                 @RequestParam(required = false) String domicileCountry,
                                                 @RequestParam(required = false) String domicileProvince,
                                                 @RequestParam(required = false) String domicileDistrict,
                                                 @RequestParam(required = false) String domicileSector,
                                                 @RequestParam(required = false) String domicileCell,
                                                 @RequestParam(required = false) String domicileVillage,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date license_from_date,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date  license_to_date,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date register_from_date,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date  register_to_date,
                                                 @RequestParam(value = "skipPagination", required = false) Boolean skipPagination,
                                                 HttpServletRequest request) {
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.GET, request.getQueryString(), null, request, "ALL");
        Query query;
        PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by("createdAt"));
        if (skipPagination != null && skipPagination) {
            query = new Query();
        } else {
            query = new Query().with(PageRequest.of(page - 1, limit));
        }

        if (licenseNumber != null && !licenseNumber.isEmpty())
            query.addCriteria(Criteria.where("licenseNumber").regex(licenseNumber, "i"));

        if (domicileCountry != null && !domicileCountry.isEmpty())
            query.addCriteria(Criteria.where("domicileCountry").regex(domicileCountry, "i"));


        if (domicileProvince != null && !domicileProvince.isEmpty())
            query.addCriteria(Criteria.where("domicileProvince").regex(domicileProvince, "i"));

        if (domicileDistrict != null && !domicileDistrict.isEmpty())
            query.addCriteria(Criteria.where("domicileDistrict").regex(domicileDistrict, "i"));

        if (domicileSector != null && !domicileSector.isEmpty())
            query.addCriteria(Criteria.where("domicileSector").regex(domicileSector, "i"));


        if (domicileCell != null && !domicileCell.isEmpty())
            query.addCriteria(Criteria.where("domicileCell").regex(domicileCell, "i"));

        if (domicileVillage != null && !domicileVillage.isEmpty())
            query.addCriteria(Criteria.where("domicileVillage").regex(domicileVillage, "i"));

        if (license_status != null && !license_status.isEmpty())
            query.addCriteria(Criteria.where("license_status").regex(license_status, "i"));

        if (postNames != null && !postNames.isEmpty())
            query.addCriteria(Criteria.where("postNames").regex(postNames, "i"));

        if (license_from_date != null && license_to_date != null)
            query.addCriteria(Criteria.where("licenseExpiryDate").gte(license_from_date).lte(setEndOfDay(license_to_date)));

        if (register_from_date != null && register_to_date != null)
            query.addCriteria(Criteria.where("createdAt").gte(register_from_date).lte(setEndOfDay(register_to_date)));

        if (facilityName != null && !facilityName.isEmpty()) {
            ProviderRegistry providerRegistry = new ProviderRegistry();
            providerRegistry.setCountryOfBirth(facilityName);
            List<FacilityRegistry> facilityRegistries = providerRegistryService.getFR(providerRegistry);
            List<String> s = facilityRegistries.stream().map(FacilityRegistry::get_id).collect(Collectors.toList());
            query.addCriteria(Criteria.where("facilityId").in(s));
        }

        if (surName != null && !surName.isEmpty()) {
            query.addCriteria(Criteria.where("surName").regex(surName, "i"));
        }

        if (documentNumber != null && !documentNumber.isEmpty()) {
            query.addCriteria(Criteria.where("documentNumber").regex(documentNumber, "i"));
        }

        if (status != null && !status.isEmpty()) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        Query finalQuery = query;

        Page<ProviderRegistry> providerRegistryPage = PageableExecutionUtils.getPage(
                mongoTemplate.find(finalQuery, ProviderRegistry.class),
                pageRequest,
                () -> mongoTemplate.count(finalQuery.skip(0).limit(0), ProviderRegistry.class));

        List<ProviderRegistry> providerRegistries = new ArrayList<>();
        for (ProviderRegistry providerRegistry : providerRegistryPage) {
            try {
                ProviderRegistry registry = new ProviderRegistry();
                registry.setFacilities(providerRegistry.getFacilities());
                providerRegistry.setFacilities(providerRegistryService.getFR(providerRegistry));
                providerRegistry.setStatus(providerRegistry.getStatus().toUpperCase());

            } catch (Exception e) {
                e.printStackTrace();
            }


            providerRegistries.add(providerRegistry);
        }


        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(providerRegistries).setCount(providerRegistryPage.getTotalElements()), HttpStatus.ACCEPTED);
    }

    @GetMapping("/activate/{id}/{status}")
    public ResponseEntity<CommonResponse> activeInActive(@PathVariable String id, @PathVariable String status, HttpServletRequest request) {
        Optional<ProviderRegistry> u = repository.findById(id);
        Optional<ProviderRegistry> oldata = repository.findById(id);

        u.get().setStatus(status);
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.UPDATE, id, oldata, u.get(), request, u.get().getSurName());
        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(repository.save(u.get())), HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<CommonResponse> delete(@PathVariable String id, HttpServletRequest request) {
        Optional<ProviderRegistry> u = repository.findById(id);
        Optional<ProviderRegistry> oldata = repository.findById(id);
        u.get().setStatus("DELETED");
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.UPDATE, id, oldata, u.get(), request, u.get().getSurName());
        return new ResponseEntity<>(new CommonResponse("ok", null).setData(repository.save(u.get())), HttpStatus.ACCEPTED);
    }


    @GetMapping("/getProviderByFacilityId/{id}")
    public ResponseEntity<?> getProviderByFacilityId(@PathVariable String id, HttpServletRequest request) {
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.UPDATE, id, null, request, "ALL");
        Query query = new Query();

        if (id != null) {
            query.addCriteria(Criteria.where("facilityId").in(id));
        }

        query.addCriteria(Criteria.where("status").is("ACTIVE"));


        List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));

        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }
    //, consumes = {MULTIPART_FORM_DATA_VALUE}

    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = {"*/*"})
    public ResponseEntity<CommonResponse> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.UPDATE, "", null, request);
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                String res = excelService.save(file);

                if (res.isEmpty()) {
                    String message = "Uploaded the file successfully: " + file.getOriginalFilename();
                    return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse("success", message));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse("error", res));
                }
            } catch (Exception e) {

                String message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CommonResponse("failed", message));
            }
        }
        String message = "Please Upload an Excel File";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse("failed", message));
    }


    @PostMapping("/save-all")
    public ResponseEntity<CommonResponse> saveAll(@RequestBody List<BulkUploadRequest> bulkUploadRequests, HttpServletRequest request) {
        auditService.saveAuditLog(getLoggedInUser(), ProviderRegistry.class.getSimpleName(), Action.ADD, bulkUploadRequests, null, request, "BULK-UPLOAD");
        try {
            String res = providerRegistryService.saveAll(bulkUploadRequests);
            if (res.isEmpty()) {
                String message = "Uploaded the file successfully: ";
                return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse("success", message));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse("error", res));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CommonResponse("failed", e.getMessage()));
        }
    }


    private boolean isAlreadyExist(String exstingId, String licNo, String docNo, String nin, String nid) {
        boolean isAlreadyExist = false;

        if (licNo != null || docNo != null || nin != null || nid != null) {
            Query query = new Query();

            if (licNo != null && !licNo.isEmpty()) {
                query.addCriteria(Criteria.where("licenseNumber").is(licNo));
                List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));
                isAlreadyExist = isUserIdSame(list, exstingId);
                if (isAlreadyExist) return true;
            }

            if (docNo != null && !docNo.isEmpty()) {
                query = new Query();
                query.addCriteria(Criteria.where("documentNumber").regex(docNo, "i"));
                List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));
                isAlreadyExist = isUserIdSame(list, exstingId);
                if (isAlreadyExist) return true;
            }

            if (nin != null && !nin.isEmpty()) {
                query = new Query();
                query.addCriteria(Criteria.where("nin").regex(nin, "i"));
                List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));
                isAlreadyExist = isUserIdSame(list, exstingId);
                if (isAlreadyExist) return true;
            }

            if (nid != null && !nid.isEmpty()) {
                query = new Query();
                query.addCriteria(Criteria.where("nid").regex(nid, "i"));
                List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));
                isAlreadyExist = isUserIdSame(list, exstingId);
                if (isAlreadyExist) return true;
            }

            /*if (identifier != null && !identifier.isEmpty()) {
                query = new Query();
                query.addCriteria(Criteria.where("identifier").regex(identifier, "i"));
                List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));
                isAlreadyExist = isUserIdSame(list, exstingId);
                if (isAlreadyExist) return true;
            }*/

            List<ProviderRegistry> list = new ArrayList<>(mongoTemplate.find(query, ProviderRegistry.class));

            return isUserIdSame(list, exstingId);
        }

        return false;
    }

    private boolean isUserIdSame(List<ProviderRegistry> list, String exstingId) {
        if (list.isEmpty())
            return false;

        if (list.size() > 1) return true;

        ProviderRegistry registry = list.get(0);

        if (exstingId != null) {
            return !(registry.get_id().equalsIgnoreCase(exstingId));
        } else {
            return true;
        }


    }
}




