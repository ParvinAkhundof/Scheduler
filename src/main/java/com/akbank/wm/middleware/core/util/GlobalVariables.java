package com.akbank.wm.middleware.core.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class GlobalVariables {

    private static Map<String, String> systemVariables;

    private static String mambuLoginAuth;

    private static Map<String, String> mambuLoginToken;

    private static Map<String, LocalDateTime> mambuLastLoginDateTime;

    private static int mambuLoginSessionTimeout;

    private GlobalVariables() {

        setMambuLoginSessionTimeout(10);
    }

    public static void setMambuLoginSessionTimeout(int newMambuLoginSessionTimeout) {

        mambuLoginSessionTimeout = newMambuLoginSessionTimeout;

    }

    public static int getMambuLoginSessionTimeout() {

        return mambuLoginSessionTimeout;

    }

    public static void setSystemVariables(Map<String, String> newSystemVariables) {

        systemVariables = newSystemVariables;

    }

    public static Map<String, String> getSystemVariables() {

        return systemVariables;

    }

    public static void setMambuLoginAuth(String newMambuLoginAuth) {

        mambuLoginAuth = newMambuLoginAuth;

    }

    public static String getMambuLoginAuth() {

        return mambuLoginAuth;

    }

    public static void setMambuLoginToken(String domain, String loginToken) {

        Map<String, String> newMambuLoginToken = new HashMap<>();
        newMambuLoginToken.put(domain, loginToken);
        mambuLoginToken = newMambuLoginToken;

        setMambuLastLoginDateTime(domain, LocalDateTime.now());
    }

    public static Map<String, String> getMambuLoginToken() {

        return mambuLoginToken;

    }

    public static void setMambuLastLoginDateTime(String domain, LocalDateTime lastLoginDateTime) {

        Map<String, LocalDateTime> newMambuLastLoginDateTime = new HashMap<>();
        newMambuLastLoginDateTime.put(domain, lastLoginDateTime);

        mambuLastLoginDateTime = newMambuLastLoginDateTime;

    }

    public static Map<String, LocalDateTime> getMambuLastLoginDateTime() {

        return mambuLastLoginDateTime;

    }
}
