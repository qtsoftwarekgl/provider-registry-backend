package com.frpr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("facility_registry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityRegistry extends AuditModel {

    @Id
    private String _id;
    private String name;


    private String code; // unique field
    private String status;


    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String locationCode; // unique field
    private String category;
    private String type;
}
