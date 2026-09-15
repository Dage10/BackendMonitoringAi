package com.david.monitoring.auth;

import com.david.monitoring.auth.dto.AuthResponse;
import com.david.monitoring.auth.dto.LoginRequest;
import com.david.monitoring.auth.dto.RegisterRequest;
import com.david.monitoring.config.AuditLogger;
import com.david.monitoring.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Environment environment;
    private final AuditLogger auditLogger;
    private final long jwtExpirationMillis;

    public AuthController(AuthService authService,
                          JwtTokenProvider jwtTokenProvider,
                          Environment environment,
                          AuditLogger auditLogger,
                          @Value("${jwt.expiration-millis:86400000}") long jwtExpirationMillis) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.environment = environment;
        this.auditLogger = auditLogger;
        this.jwtExpirationMillis = jwtExpirationMillis;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletRequest httpRequest,
                                                 HttpServletResponse response) {
        User user = authService.register(request);
        String token = createToken(user);
        setCookie(response, token);
        auditLogger.register(user.getUsername(), user.getEmail(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(toAuthResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse response) {
        try {
            User user = authService.login(request);
            String token = createToken(user);
            setCookie(response, token);
            auditLogger.loginSuccess(user.getUsername(), httpRequest.getRemoteAddr());
            return ResponseEntity.ok(toAuthResponse(user));
        } catch (ResponseStatusException ex) {
            auditLogger.loginFailed(request.username(), httpRequest.getRemoteAddr(), "Invalid credentials");
            throw ex;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        CookieUtils.clearTokenCookie(response, environment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User user = authService.findById(userId);
            return ResponseEntity.ok(toAuthResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String createToken(User user) {
        return jwtTokenProvider.createToken(user.getId(), user.getUsername(), user.getEmail());
    }

    private void setCookie(HttpServletResponse response, String token) {
        CookieUtils.setTokenCookie(response, token, Math.min((int)(jwtExpirationMillis / 1000), Integer.MAX_VALUE), environment);
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
