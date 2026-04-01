package com.lssl.spacecolony.model.mission;

import java.util.List;

public class MissionResult {
    private final boolean success;
    private final String summary;
    private final List<String> logs;

    public MissionResult(boolean success, String summary, List<String> logs) {
        this.success = success;
        this.summary = summary;
        this.logs = logs;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getLogs() {
        return logs;
    }
}