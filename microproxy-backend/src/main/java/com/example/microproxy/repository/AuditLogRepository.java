package com.example.microproxy.repository;

import com.example.microproxy.model.AuditLog;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class AuditLogRepository {

    private final List<AuditLog> logs = new CopyOnWriteArrayList<>();

    public void save(AuditLog log) {
        logs.add(log);
    }

    public List<AuditLog> findAll() {
        return new ArrayList<>(logs);
    }

    public long count() {
        return logs.size();
    }
}
