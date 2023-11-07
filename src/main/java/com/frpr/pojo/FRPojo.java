package com.frpr.pojo;

import com.frpr.model.*;
import lombok.Data;

@Data
public class FRPojo {
    private Provinces province;
    private Districts district;
    private Sectors sector;
    private Cell cell;
    private Villages village;
    private String _id;
    private String name;
    private String code;
    private String status;
    private String locationCode;
    private String category;
    private String type;

    public FRPojo(FacilityRegistry facilityRegistry){
        this._id = facilityRegistry.get_id();
        this.name = facilityRegistry.getName();
        this.code = facilityRegistry.getCode();
        this.status = facilityRegistry.getStatus();
        this.locationCode = facilityRegistry.getLocationCode();
        this.category = facilityRegistry.getCategory();
        this.type = facilityRegistry.getType();
    }

}
