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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

        // Customize token generation as needed
        String primaryRole = roles.isEmpty() ? "USER" : roles.get(0);
        return jwtUtil.generateToken(username, primaryRole);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllUsersWithRoles();
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countAllUsers());

        List<Object[]> usersByRole = userRepository.countUsersByRole();
        Map<String, Long> roleCounts = new HashMap<>();
        for (Object[] row : usersByRole) {
            @SuppressWarnings("unchecked")
            Set<Role> roles = (Set<Role>) row[0];
            Long count = (Long) row[1];
            for (Role role : roles) {
                roleCounts.put(role.name(), roleCounts.getOrDefault(role.name(), 0L) + count);
            }
        }
        stats.put("usersByRole", roleCounts);

        stats.put("pendingApprovals", userRepository.countPendingApprovals());

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        stats.put("recentUsers", userRepository.findRecentUsers(sevenDaysAgo));

        return stats;
    }

    @Override
    public DashboardResponse getDashboardData() {
        DashboardResponse.UserStatusCounts counts = new DashboardResponse.UserStatusCounts();
        counts.setApproved(userRepository.countByApprovalStatus(ApprovalStatus.APPROVED));
        counts.setPending(userRepository.countByApprovalStatus(ApprovalStatus.PENDING));
        counts.setRejected(userRepository.countByApprovalStatus(ApprovalStatus.REJECTED));

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        LocalDateTime currentStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime currentEnd = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        LocalDateTime previousStart = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime previousEnd = currentStart;

        DashboardResponse.MonthlyHeadcount headcount = new DashboardResponse.MonthlyHeadcount();
        headcount.setPreviousMonth(userRepository.countApprovedBetween(previousStart, previousEnd));
        headcount.setCurrentMonth(userRepository.countApprovedBetween(currentStart, currentEnd));

        DashboardResponse response = new DashboardResponse();
        response.setUserStatusCounts(counts);
        response.setMonthlyHeadcount(headcount);

        return response;
    }
}
