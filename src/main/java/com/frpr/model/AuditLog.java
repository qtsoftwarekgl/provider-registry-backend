package com.frpr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("audit_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog extends AuditModel{

    @Id
    private String _id;
    private String userName;
    private String entity;
    private String path;
    private Action action;
    private String httpMethod;
    private String payload;
    private String oldData;
    private String newData;
    private String ip;
    private String providerName;

    public AuditLog(String userName, String entity, Action action, String payload, String oldData, String ip, String httpMethod, String path) {
        this.userName = userName;
        this.action = action;
        this.entity = entity;
        this.payload = payload;
        this.oldData = oldData;
        this.ip = ip;
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public AuditLog(String userName, String entity, Action action, String payload, String oldData, String ip, String httpMethod, String path, String providerName) {
        this.userName = userName;
        this.action = action;
        this.entity = entity;
        this.payload = payload;
        this.oldData = oldData;
        this.ip = ip;
        this.httpMethod = httpMethod;
        this.path = path;
        this.providerName = providerName;
    }

    public AuditLog(String userName, String entity, Action action, String payload, String oldData, String ip, String httpMethod, String path, String providerName, String newData) {
        this.userName = userName;
        this.action = action;
        this.entity = entity;
        this.payload = payload;
        this.oldData = oldData;
        this.ip = ip;
        this.httpMethod = httpMethod;
        this.path = path;
        this.providerName = providerName;
        this.newData = newData;
    }
}