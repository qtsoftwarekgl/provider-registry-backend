package com.frpr.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public abstract class AuditModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
