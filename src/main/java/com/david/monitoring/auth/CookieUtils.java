package com.david.monitoring.auth;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;

public final class CookieUtils {

    public static final String TOKEN_COOKIE = "token";

    private CookieUtils() {}

    public static void setTokenCookie(HttpServletResponse response, String token, int maxAgeSeconds, Environment env) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(TOKEN_COOKIE, token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds));

        if (isProd(env)) {
            builder.secure(true).sameSite("None");
        } else {
            builder.secure(false).sameSite("Lax");
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public static void clearTokenCookie(HttpServletResponse response, Environment env) {
        setTokenCookie(response, "", 0, env);
    }

    private static boolean isProd(Environment env) {
        for (String profile : env.getActiveProfiles()) {
            if ("prod".equals(profile)) return true;
        }
        return false;
    }
}
