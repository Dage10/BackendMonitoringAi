package com.david.monitoring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long EVICT_AFTER_MS = 120_000;
    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        evictStaleEntries();
        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        RequestCounter counter = counters.compute(ip, (key, existing) -> {
            if (existing == null || now - existing.windowStart > 60_000) {
                return new RequestCounter(now, 1);
            }
            existing.count++;
            return existing;
        });

        if (counter.count > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void evictStaleEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, RequestCounter>> it = counters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, RequestCounter> entry = it.next();
            if (now - entry.getValue().windowStart > EVICT_AFTER_MS) {
                it.remove();
            }
        }
    }

    private static class RequestCounter {
        long windowStart;
        int count;

        RequestCounter(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
