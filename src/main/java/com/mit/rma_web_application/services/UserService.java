package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.DashboardResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.models.Role;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.interfaces.IUserService;
import com.mit.rma_web_application.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Override
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setApprovedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public String generateToken(String username) {
        User user = findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList());

        // You can customize how you generate token, for example:
        // Use first role for token claim (if only one), or generate token per role as needed
        String primaryRole = roles.isEmpty() ? "USER" : roles.get(0);

        return jwtUtil.generateToken(username, primaryRole);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());

        // Count users by role (simplified)
        Map<String, Long> roleCounts = new HashMap<>();
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            for (Role role : u.getRoles()) {
                roleCounts.put(role.name(), roleCounts.getOrDefault(role.name(), 0L) + 1);
            }
        }
        stats.put("usersByRole", roleCounts);

        // Pending approvals count
        stats.put("pendingApprovals", userRepository.findByApprovalStatus(ApprovalStatus.PENDING).size());

        // You can add more stats here...

        return stats;
    }

    @Override
    public DashboardResponse getDashboardData() {
        DashboardResponse.UserStatusCounts counts = new DashboardResponse.UserStatusCounts();
        counts.setApproved(userRepository.findByApprovalStatus(ApprovalStatus.APPROVED).size());
        counts.setPending(userRepository.findByApprovalStatus(ApprovalStatus.PENDING).size());
        counts.setRejected(userRepository.findByApprovalStatus(ApprovalStatus.REJECTED).size());

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        LocalDateTime currentStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime currentEnd = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        LocalDateTime previousStart = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime previousEnd = currentStart;

        DashboardResponse.MonthlyHeadcount headcount = new DashboardResponse.MonthlyHeadcount();
        headcount.setPreviousMonth(
                (int) userRepository.findByApprovalStatus(ApprovalStatus.APPROVED).stream()
                        .filter(u -> u.getApprovedAt() != null &&
                                !u.getApprovedAt().isBefore(previousStart) &&
                                u.getApprovedAt().isBefore(previousEnd))
                        .count()
        );
        headcount.setCurrentMonth(
                (int) userRepository.findByApprovalStatus(ApprovalStatus.APPROVED).stream()
                        .filter(u -> u.getApprovedAt() != null &&
                                !u.getApprovedAt().isBefore(currentStart) &&
                                u.getApprovedAt().isBefore(currentEnd))
                        .count()
        );

        DashboardResponse response = new DashboardResponse();
        response.setUserStatusCounts(counts);
        response.setMonthlyHeadcount(headcount);

        return response;
    }
}
