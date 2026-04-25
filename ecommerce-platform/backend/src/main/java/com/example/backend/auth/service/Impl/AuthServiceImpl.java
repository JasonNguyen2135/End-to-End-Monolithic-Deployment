package com.example.backend.auth.service.Impl;

import com.example.backend.auth.dto.Requests.*;
import com.example.backend.auth.dto.Responses.*;
import com.example.backend.auth.exception.*;
import com.example.backend.entity.*;
import com.example.backend.exception.*;
import com.example.backend.auth.service.AuthService;
import com.example.backend.repository.*;
import com.example.backend.util.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepo verificationTokenRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTserviceImpl jwtService;
    private final OTPRepository otpRepository;
    private final PasswordResetAttemptRepository attemptRepo;

    @Override
    @Transactional
    public RegisterResponse register(SignUpRequest signUpRequest , MultipartFile file) throws IOException {
        if (usersRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("The new email is already in use.");
        }
        Role userRole = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.ROLE_USER;
        
        Users user = Users.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .role(userRole)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .enabled(true) // KÍCH HOẠT LUÔN
                .emailVerified(true) // XÁC THỰC LUÔN
                .createdAt(LocalDateTime.now())
                .profileImageUrl("https://via.placeholder.com/150")
                .build();

        usersRepo.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepo.save(verificationToken);

        String verificationLink = "http://localhost:8081/api/v1/auth/verify-email?token=" + token;
        String body = "Hello " + user.getFirstName() + ",\n\n" +
                "Click the link to verify your account:\n" + verificationLink;
        
        try {
            emailService.sendEmail(user.getEmail(), "Verify your account", body);
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Could not send verification email. Error: " + e.getMessage());
            System.out.println("🔗 Verification Link for manual use: " + verificationLink);
        }

        return new RegisterResponse("User registered. Please check your email (or logs) for verification." ,token);
    }

    @Override
    @Transactional
    public MessageResponse verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verificationToken.isUsed() || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new MessageResponse("Token invalid or expired");
        }

        Users user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEnabled(true);
        usersRepo.save(user);
        verificationToken.setUsed(true);
        verificationTokenRepo.save(verificationToken);

        return new MessageResponse("Email verified successfully!");
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
    public LoginResponse refreshToken(RefreshTokenReq refreshTokenReq) {
        String email;
        try {
            email = jwtService.extractUsername(refreshTokenReq.getToken());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (!jwtService.validateToken(refreshTokenReq.getToken(), user)) {
            throw new InvalidTokenException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtService.generateToken(user);

        return new LoginResponse(
                "Token refreshed successfully",
                newAccessToken,
                refreshTokenReq.getToken(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl(),
                user.getRole()
        );
    }

    @Override
    public MessageResponse logout(String email, String refreshToken) {
        return new MessageResponse("Logged out successfully");
    }

    @Override
    @Transactional
    public MessageResponse updatePassword(UpdatePasswordRequest request, String currentUserEmail) {
        Users user = usersRepo.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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

    public GetProfileResponse getUserProfile(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new GetProfileResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }

    @Transactional
    public MessageResponse deleteCurrentUser(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        verificationTokenRepo.deleteByUser(user);
        usersRepo.delete(user);
        return new MessageResponse("User Deleted Successfully");
    }

    @Transactional
    public UpdateEmailRequest requestEmailUpdate(String currentEmail, String newEmail) {
        Users user = usersRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (usersRepo.findByEmail(newEmail).isPresent()) {
            throw new EmailAlreadyUsedException("The new email is already in use.");
        }
        
        VerificationToken token = verificationTokenRepo.findByUser(user)
                .orElse(new VerificationToken());
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        token.setUsed(false);
        token.setNewEmail(newEmail);
        verificationTokenRepo.save(token);

        return new UpdateEmailRequest(
                "Verification email sent.",
                newEmail,
                token.getToken()
        );
    }

    @Transactional
    public UpdateEmailResponse verifyEmailUpdate(String tokenStr) {
        VerificationToken token = verificationTokenRepo.findByToken(tokenStr)
                .orElseThrow(() -> new InvalidTokenException("The verification token is invalid or expired."));

        if (token.isUsed() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token invalid or expired.");
        }

        Users user = token.getUser();
        user.setEmail(token.getNewEmail());
        token.setUsed(true);
        usersRepo.save(user);
        verificationTokenRepo.save(token);
        return new UpdateEmailResponse("Email updated successfully" , user.getEmail());
    }

    public @Nullable List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    @Transactional
    @Override
    public ForgetPasswordResponse forgotPassword(String currentEmail) {
        Users user = usersRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        PasswordResetAttempt attempt = attemptRepo.findByEmail(currentEmail)
                        .orElse(new PasswordResetAttempt());

        if (attempt.getAttempts() >= 3 &&
                attempt.getLastAttempt().isAfter(LocalDateTime.now().minusHours(1))) {
            throw new TooManyRequestsException("Too many reset attempts.");
        }

        attempt.setEmail(currentEmail);
        attempt.setAttempts(attempt.getAttempts() + 1);
        attempt.setLastAttempt(LocalDateTime.now());
        attemptRepo.save(attempt);

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        otpRepository.deleteByUser(user);

        OTP otpEntity = new OTP();
        otpEntity.setUser(user);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        otpRepository.save(otpEntity);

        emailService.sendEmail(user.getEmail(), "Password Reset OTP", "Your OTP is: " + otp);

        return new ForgetPasswordResponse("OTP sent successfully." , otp);
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        Users user = usersRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        OTP otpEntity = otpRepository.findByUserAndOtp(user, request.getOtp())
                .orElseThrow(() -> new InvalidOtpException("Invalid OTP"));

        if (otpEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity);
            throw new InvalidOtpException("OTP expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepo.save(user);
        otpRepository.delete(otpEntity);

        return new MessageResponse("Password reset successfully");
    }
}
