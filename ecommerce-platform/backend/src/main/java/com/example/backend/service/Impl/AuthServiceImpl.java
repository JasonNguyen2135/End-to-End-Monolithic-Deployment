package com.example.backend.service.Impl;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.exception.*;
import com.example.backend.service.AuthService;
import com.example.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTserviceImpl jwtService;

    @Override
    @Transactional
    public RegisterResponse register(SignUpRequest signUpRequest , MultipartFile file) throws IOException {
        if (usersRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use.");
        }
        Role userRole = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.ROLE_USER;
        
        Users user = Users.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .role(userRole)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .enabled(true)
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .profileImageUrl("https://via.placeholder.com/150")
                .build();

        usersRepo.save(user);
        return new RegisterResponse("User registered successfully." ,"SUCCESS");
    }

    @Override
    public LoginResponse login(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Users user = usersRepo.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse (
            "Login successful",
            token, refreshToken,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getProfileImageUrl(),
            user.getRole()
        );
    }

    @Override
    @Transactional
    public MessageResponse updatePassword(UpdatePasswordRequest request, String currentUserEmail) {
        Users user = usersRepo.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepo.save(user);

        return new MessageResponse("Password updated successfully");
    }

    @Override
    public UpdateProfileResponse updateProfile(String userEmail, String firstName, String lastName, MultipartFile file) throws IOException {
        Users user = usersRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);

        usersRepo.save(user);

        return new UpdateProfileResponse(
                "Profile updated successfully",
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }

    @Override
    public GetProfileResponse getUserProfile(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return new GetProfileResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }

    @Override
    @Transactional
    public MessageResponse deleteCurrentUser(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        usersRepo.delete(user);
        return new MessageResponse("User deleted successfully");
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }
}
