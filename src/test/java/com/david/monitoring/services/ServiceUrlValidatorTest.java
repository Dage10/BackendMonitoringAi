package com.david.monitoring.services;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceUrlValidatorTest {

    private final ServiceUrlValidator validator = new ServiceUrlValidator();

    @Test
    void validPublicUrl() {
        assertDoesNotThrow(() -> validator.validate("https://example.com"));
    }

    @Test
    void validHttpUrl() {
        assertDoesNotThrow(() -> validator.validate("http://httpbin.org/get"));
    }

    @Test
    void rejectsFtpScheme() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("ftp://example.com/file"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsNullHost() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("https://"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsLocalhost() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("http://localhost:8080"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsLoopbackIp() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("http://127.0.0.1"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsPrivateIp10() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("http://10.0.0.1"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsPrivateIp192() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("http://192.168.1.1"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsPrivateIp172() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("http://172.16.0.1"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsUrlWithUserInfo() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("https://user:pass@example.com"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsUrlWithFragment() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("https://example.com/page#section"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsInvalidUrl() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("not-a-url"));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void rejectsLinkLocal() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> validator.validate("http://169.254.169.254"));
        assertEquals(400, ex.getStatusCode().value());
    }
}
