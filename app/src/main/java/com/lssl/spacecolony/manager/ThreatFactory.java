package com.lssl.spacecolony.manager;

import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.mission.Threat;

import java.util.Random;

public class ThreatFactory {

    private static int completedMissionCount = 0;
    private static final Random random = new Random();

    public static Threat createThreat() {
        MissionType[] missionTypes = MissionType.values();
        MissionType type = missionTypes[random.nextInt(missionTypes.length)];

        int baseSkill = 4 + completedMissionCount;
        int baseResilience = 1 + (completedMissionCount / 2);
        int baseEnergy = 18 + completedMissionCount * 2;

        String name = getThreatName(type);

        return new Threat(name, type, baseSkill, baseResilience, baseEnergy);
    }

    public static void recordMissionSuccess() {
        completedMissionCount++;
    }

    public static int getCompletedMissionCount() {
        return completedMissionCount;
    }

    public static void setCompletedMissionCount(int count) {
        completedMissionCount = Math.max(0, count);
    }

    private static String getThreatName(MissionType type) {
        switch (type) {
            case COMBAT:
                return "Alien Raiders";
            case REPAIR:
                return "Critical System Failure";
            case EXPLORATION:
                return "Unknown Space Anomaly";
            case MEDICAL:
                return "Biohazard Outbreak";
            case RESEARCH:
                return "Quantum Instability";
            case NAVIGATION:
                return "Asteroid Storm";
            default:
                return "Unknown Threat";
        }
    }
}