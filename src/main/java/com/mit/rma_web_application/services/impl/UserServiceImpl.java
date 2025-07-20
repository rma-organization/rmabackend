package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.dtos.DashboardResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken.");
        }

        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(encodedPassword);
        user.setRoles(registerRequestDTO.getRoles());

        return userRepository.save(user);
    }


    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", userRepository.countAllUsers());

    /**
     * Returns a general stats map, useful for flexible UI displays.
     */
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countAllUsers());

        // Users by role
        List<Object[]> usersByRole = userRepository.countUsersByRole();
        Map<String, Long> roleCounts = new HashMap<>();
        for (Object[] row : usersByRole) {
            @SuppressWarnings("unchecked")
            var roles = (java.util.Set<com.mit.rma_web_application.models.Role>) row[0];
            Long count = (Long) row[1];
            for (var role : roles) {
                roleCounts.put(role.name(), roleCounts.getOrDefault(role.name(), 0L) + count);
            }
        }
        stats.put("usersByRole", roleCounts);

        // Pending approvals
        stats.put("pendingApprovals", userRepository.countPendingApprovals());

        // Recently registered users (last 7 days)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        stats.put("recentUsers", userRepository.findRecentUsers(sevenDaysAgo));

        return stats;
    }

    /**
     * Returns structured dashboard data including user status counts and monthly headcounts.
     */
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

