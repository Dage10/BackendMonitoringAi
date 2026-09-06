package com.david.monitoring.auth;

import com.david.monitoring.auth.dto.LoginRequest;
import com.david.monitoring.auth.dto.RegisterRequest;
import com.david.monitoring.entities.User;
import com.david.monitoring.users.UserRepository;
import com.david.monitoring.users.UserService;
import com.david.monitoring.users.dto.CreateUserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       UserService userService,
                       PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        String passwordHash = passwordEncoder.encode(request.password());

        CreateUserRequest createUserRequest = new CreateUserRequest(
                request.username(),
                request.email(),
                request.password()
        );

        return userService.createUser(createUserRequest, passwordHash);
    }

    public User login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return user;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}
