package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.config.JwtUtil;
import com.mit.rma_web_application.dtos.DashboardResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.models.Role;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User registerUser(RegisterRequestDTO registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken.");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRoles(registrationDto.getRoles());
        user.setApprovalStatus(ApprovalStatus.PENDING);

        return userRepository.save(user);
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());

        // Example: count users by role (simple example)
        Map<String, Long> roleCounts = new HashMap<>();
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            for (Role role : user.getRoles()) {
                roleCounts.put(role.name(), roleCounts.getOrDefault(role.name(), 0L) + 1);
            }
        }
        stats.put("usersByRole", roleCounts);

        stats.put("pendingApprovals", userRepository.countByApprovalStatus(ApprovalStatus.PENDING));

        // Add other stats as needed

        return stats;
    }

    @Override
    public DashboardResponse getDashboardData() {
        DashboardResponse response = new DashboardResponse();

        // Set counts by approval status
        DashboardResponse.UserStatusCounts counts = new DashboardResponse.UserStatusCounts();
        counts.setApproved(userRepository.countByApprovalStatus(ApprovalStatus.APPROVED));
        counts.setPending(userRepository.countByApprovalStatus(ApprovalStatus.PENDING));
        counts.setRejected(userRepository.countByApprovalStatus(ApprovalStatus.REJECTED));
        response.setUserStatusCounts(counts);

        // Monthly headcount example
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        long currentMonthCount = userRepository.countByApprovalStatusAndApprovedAtBetween(ApprovalStatus.APPROVED, startOfMonth, startOfNextMonth);
        response.setMonthlyHeadcount(new DashboardResponse.MonthlyHeadcount(0, currentMonthCount)); // previousMonth count as 0 for example

        return response;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Override
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setApprovedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
