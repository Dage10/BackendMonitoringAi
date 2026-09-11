package com.david.monitoring.auth;

import com.david.monitoring.auth.dto.LoginRequest;
import com.david.monitoring.auth.dto.RegisterRequest;
import com.david.monitoring.config.AuditLogger;
import com.david.monitoring.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private AuthController controller;

    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authService, jwtTokenProvider,
                new StandardEnvironment(), auditLogger, 86400000);
    }

    private User createUser(Long id) throws Exception {
        User user = new User("testuser", "test@example.com", "hashedpassword");
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private Authentication auth(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, List.of());
    }

    @Test
    void registerReturnsOk() throws Exception {
        User user = createUser(1L);
        when(authService.register(any(RegisterRequest.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.register(
                new RegisterRequest("testuser", "test@example.com", "password123"),
                httpRequest, httpResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditLogger).register(eq("testuser"), eq("test@example.com"), any());
    }

    @Test
    void registerWithInvalidData() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        assertThrows(ResponseStatusException.class,
                () -> controller.register(
                        new RegisterRequest("", "", ""),
                        httpRequest, httpResponse));
    }

    @Test
    void loginReturnsOk() throws Exception {
        User user = createUser(1L);
        when(authService.login(any(LoginRequest.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.login(
                new LoginRequest("testuser", "password123"),
                httpRequest, httpResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditLogger).loginSuccess(eq("testuser"), any());
    }

    @Test
    void loginWithInvalidCredentials() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        assertThrows(ResponseStatusException.class,
                () -> controller.login(
                        new LoginRequest("testuser", "wrongpassword"),
                        httpRequest, httpResponse));
    }

    @Test
    void logoutReturnsOk() {
        ResponseEntity<?> response = controller.logout(httpResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void meReturnsOk() throws Exception {
        User user = createUser(1L);
        when(authService.findById(1L)).thenReturn(user);

        ResponseEntity<?> response = controller.me(auth(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void meWithoutAuthReturnsUnauthorized() {
        ResponseEntity<?> response = controller.me(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void meWithNonLongPrincipalReturnsUnauthorized() {
        Authentication badAuth = mock(Authentication.class);
        when(badAuth.getPrincipal()).thenReturn("not-a-long");

        ResponseEntity<?> response = controller.me(badAuth);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
