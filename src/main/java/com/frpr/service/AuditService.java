package com.frpr.service;

import com.frpr.model.Action;
import com.frpr.model.AuditLog;
import com.frpr.repo.AuditLogRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuditService {

    @Autowired
    AuditLogRepository auditLogRepository;

    @Async
    public void saveAuditLog(String username, String entity, Action action, Object payload, Object oldData, HttpServletRequest request) {
        String requestData = "";
        if (payload != null) {
            if (payload instanceof String) {
                requestData = (String) payload;
            } else {
                requestData = new Gson().toJson(payload);
            }
        }
        AuditLog auditLog = new AuditLog(username, entity, action, requestData, oldData == null ? "" : new Gson().toJson(oldData), request.getRemoteAddr(), request.getMethod(), request.getServletPath());
        auditLogRepository.save(auditLog);
    }

    @Async
    public void saveAuditLog(String username, String entity, Action action, Object payload, Object oldData, HttpServletRequest request, String providerName) {
        String requestData = "";
        if (payload != null) {
            if (payload instanceof String) {
                requestData = (String) payload;
            } else {
                requestData = new Gson().toJson(payload);
            }
        }
        System.out.println("request getRemoteAddr"+ request.getRemoteAddr());
        System.out.println("request getServletPath"+ request.getServletPath());
        AuditLog auditLog = new AuditLog(username, entity, action, requestData, oldData == null ? "" : new Gson().toJson(oldData), request.getRemoteAddr(), request.getMethod(), request.getServletPath(), providerName);
        auditLogRepository.save(auditLog);
    }

    @Async
    public void saveAuditLog(String username, String entity, Action action, Object payload, Object oldData,  Object newDate, HttpServletRequest request, String providerName) {
        String requestData = "";
        if (payload != null) {
            if (payload instanceof String) {
                requestData = (String) payload;
            } else {
                requestData = new Gson().toJson(payload);
            }
        }
        System.out.println("request getRemoteAddr"+ request.getRemoteAddr());
        System.out.println("request getServletPath"+ request.getServletPath());
        AuditLog auditLog = new AuditLog(username, entity, action, requestData, oldData == null ? "" : new Gson().toJson(oldData),
                request.getRemoteAddr(), request.getMethod(), request.getServletPath(), providerName, newDate == null ? "" : new Gson().toJson(newDate));
        auditLogRepository.save(auditLog);
    }


    public void saveAuditLogBlocking(String username, String entity, Action action, Object payload, Object oldData,  Object newData, HttpServletRequest request, String providerName) {
        String requestData = "";
        if (payload != null) {
            if (payload instanceof String) {
                requestData = (String) payload;
            } else {
                requestData = new Gson().toJson(payload);
            }
        }
        System.out.println("request getRemoteAddr"+ request.getRemoteAddr());
        System.out.println("request getServletPath"+ request.getServletPath());
        AuditLog auditLog = new AuditLog(username, entity, action, requestData, oldData == null ? "" : new Gson().toJson(oldData),
                request.getRemoteAddr(), request.getMethod(), request.getServletPath(), providerName, newData == null ? "" : new Gson().toJson(newData));
        auditLogRepository.save(auditLog);
    }
}
