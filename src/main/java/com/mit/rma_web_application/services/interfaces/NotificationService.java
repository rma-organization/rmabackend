package com.mit.rma_web_application.services.interfaces;

import java.util.List;

public interface NotificationService {
    void notifyRoles(List<String> roleNames, String message);
    void notifyUser(String username, String message);
}
