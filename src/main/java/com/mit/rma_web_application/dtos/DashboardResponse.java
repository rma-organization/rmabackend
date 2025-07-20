package com.mit.rma_web_application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private UserStatusCounts userStatusCounts;
    private MonthlyHeadcount monthlyHeadcount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatusCounts {
        private long approved;
        private long pending;
        private long rejected;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHeadcount {
        private long previousMonth;
        private long currentMonth;
    }
}
