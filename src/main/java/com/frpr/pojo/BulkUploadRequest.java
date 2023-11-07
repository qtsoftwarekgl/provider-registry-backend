package com.frpr.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BulkUploadRequest {
    private String documentType;
    private String documentNumber;
    private String surName;
    private String postNames;
    private String dateOfBirth;
    private String maritalStatus;
    private String sex;
    private String nationality;
    private String domicileCountry;
    private String domicileProvince;
    private String domicileDistrict;
    private String domicileSector;
    private String domicileCell;
    private String domicileVillage;
    private String licenseNumber;
    private String licenseExpiryDate;
    private String license_status;
    private String email;
    private String phoneNumber;
    private String qualification;
    private String status;
    private String applicationNumber;
    @JsonProperty("FacilityId")
    private String facilityId;
}