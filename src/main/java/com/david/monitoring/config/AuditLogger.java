package com.david.monitoring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    public void loginSuccess(String username, String ip) {
        log.info("LOGIN_SUCCESS user={} ip={}", username, ip);
    }

    public void loginFailed(String username, String ip, String reason) {
        log.warn("LOGIN_FAILED user={} ip={} reason={}", username, ip, reason);
    }

    public void register(String username, String email, String ip) {
        log.info("REGISTER user={} email={} ip={}", username, email, ip);
    }

    public void serviceCreated(Long userId, Long serviceId, String url) {
        log.info("SERVICE_CREATED userId={} serviceId={} url={}", userId, serviceId, url);
    }

    public void serviceUpdated(Long userId, Long serviceId) {
        log.info("SERVICE_UPDATED userId={} serviceId={}", userId, serviceId);
    }

    public void serviceDeleted(Long userId, Long serviceId) {
        log.info("SERVICE_DELETED userId={} serviceId={}", userId, serviceId);
    }

    public void anomalyDetected(Long userId, Long serviceId, String message) {
        log.warn("ANOMALY_DETECTED userId={} serviceId={} detail={}", userId, serviceId, message);
    }
}
