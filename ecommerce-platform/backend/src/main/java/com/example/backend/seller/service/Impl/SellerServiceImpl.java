package com.example.backend.seller.service.Impl;

import com.example.backend.auth.dto.Responses.MessageResponse;
import com.example.backend.auth.exception.UserNotFoundException;
import com.example.backend.entity.Role;
import com.example.backend.entity.Users;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.UsersRepo;
import com.example.backend.seller.dto.SellerRequestResponse;
import com.example.backend.seller.entity.SellerProfile;
import com.example.backend.seller.entity.SellerRequest;
import com.example.backend.seller.entity.SellerRequestStatus;
import com.example.backend.seller.exception.SellerRequestException;
import com.example.backend.seller.repository.SellerProfileRepo;
import com.example.backend.seller.repository.SellerRequestRepo;
import com.example.backend.seller.service.SellerService;
import com.example.backend.util.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final UsersRepo usersRepo;
    private final SellerRequestRepo sellerRequestRepo;
    private final SellerProfileRepo sellerProfileRepo;
    private final EmailService emailService;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    @Transactional
    public SellerRequestResponse requestSeller(String userEmail, String storeName,String reason , MultipartFile document) throws IOException {

        Users user = usersRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        sellerRequestRepo.findByUser(user)
                .filter(r -> r.getStatus() == SellerRequestStatus.PENDING)
                .ifPresent(r -> {
                    throw new SellerRequestException("You already have a pending seller request");
                });

        String documentUrl = "https://via.placeholder.com/150"; // Default URL

        SellerRequest request = SellerRequest.builder()
                .user(user)
                .storeName(storeName)
                .reason(reason)
                .documentUrl(documentUrl)
                .status(SellerRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        sellerRequestRepo.save(request);

        return toDto(request);
    }

    @Override
    @Transactional
    public MessageResponse approveRequest(Long requestId, String adminEmail) {

        SellerRequest request = sellerRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerRequest", "id", requestId));

        if (request.getStatus() != SellerRequestStatus.PENDING) {
            throw new SellerRequestException("Seller request already processed");
        }

        Users user = request.getUser();

        SellerProfile profile = sellerProfileRepo.findByUser(user)
                .orElseGet(() -> SellerProfile.builder()
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .build());

        profile.setStoreName(request.getStoreName());
        sellerProfileRepo.save(profile);

        user.setRole(Role.ROLE_SELLER);
        usersRepo.save(user);

        request.setStatus(SellerRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(adminEmail);
        sellerRequestRepo.save(request);

        emailService.sendEmail(
                user.getEmail(),
                "Seller Request Approved",
                "Congratulations!"
        );

        return new MessageResponse("Seller request approved successfully");
    }

    @Override
    @Transactional
    public MessageResponse rejectRequest(Long requestId, String adminEmail, String reason) {

        SellerRequest request = sellerRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerRequest", "id", requestId));

        if (request.getStatus() != SellerRequestStatus.PENDING) {
            throw new SellerRequestException("Seller request already processed");
        }

        request.setStatus(SellerRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(adminEmail);
        request.setReason(reason);

        sellerRequestRepo.save(request);

        emailService.sendEmail(
                request.getUser().getEmail(),
                "Seller Request Rejected",
                "Your seller request was rejected."
        );

        return new MessageResponse("Seller request rejected");
    }

    @Override
    public List<SellerRequestResponse> getPendingRequests() {
        List<SellerRequest> pendingRequests = sellerRequestRepo.findAllByStatus(SellerRequestStatus.PENDING);
        return pendingRequests.stream()
                .map(this::toDto)
                .toList();
    }

    private SellerRequestResponse toDto(SellerRequest r) {
        return new SellerRequestResponse(
                r.getId(),
                r.getUser().getEmail(),
                r.getStoreName(),
                r.getDocumentUrl(),
                r.getReason(),
                r.getStatus(),
                r.getCreatedAt().format(ISO),
                r.getReviewedAt() != null ? r.getReviewedAt().format(ISO) : null,
                r.getReviewedBy()
        );
    }
}
