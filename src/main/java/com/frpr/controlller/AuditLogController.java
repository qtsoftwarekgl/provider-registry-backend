package com.frpr.controlller;

import com.frpr.model.AuditLog;
import com.frpr.repo.AuditLogRepository;
import com.frpr.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/v1/audit-log")
public class AuditLogController {


    @Autowired
    AuditLogRepository repository;

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping
    public ResponseEntity<?> getStaticRole(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit,
                                           @RequestParam(required = false) String user, @RequestParam(required = false) String action,
                                           @RequestParam(required = false) String entity, @RequestParam(required = false) String ip,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                           @RequestParam(required = false) String providerName) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Query query = new Query().with(pageRequest);;

        if (user != null && !user.isEmpty())
            query.addCriteria(Criteria.where("userName").regex(user, "i"));

        if (action != null && !action.isEmpty())
            query.addCriteria(Criteria.where("action").regex(action, "i"));

        if (entity != null && !entity.isEmpty())
            query.addCriteria(Criteria.where("entity").regex(entity, "i"));

        if (ip != null && !ip.isEmpty())
            query.addCriteria(Criteria.where("ip").regex(ip, "ip"));

        if (startDate != null && endDate != null)
            query.addCriteria(Criteria.where("createdAt").gte(startDate).lt(endDate));

        if (providerName != null && !providerName.isEmpty())
            query.addCriteria(Criteria.where("providerName").regex(providerName, "i"));

        Page<AuditLog> logs = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, AuditLog.class),
                pageRequest,
                () -> mongoTemplate.count(query.skip(0).limit(0), AuditLog.class));

        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(logs.get()).setCount(logs.getTotalElements()), HttpStatus.ACCEPTED);
    }
}
