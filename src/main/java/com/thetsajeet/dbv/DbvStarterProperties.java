package com.thetsajeet.dbv;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dbv.springboot.starter")
public class DbvStarterProperties {

    private boolean verifyTables = true;
    private boolean verifyViews = false;

    public boolean isVerifyTables() {
        return verifyTables;
    }

    public void setVerifyTables(boolean verifyTables) {
        this.verifyTables = verifyTables;
    }

    public boolean isVerifyViews() {
        return verifyViews;
    }

    public void setVerifyViews(boolean verifyViews) {
        this.verifyViews = verifyViews;
    }
}
