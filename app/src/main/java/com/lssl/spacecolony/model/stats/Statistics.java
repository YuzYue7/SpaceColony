package com.lssl.spacecolony.model.stats;

public class Statistics {
    private int missionsAttempted;
    private int missionsWon;
    private int missionsLost;
    private int trainingSessions;
    private int totalDamageDealt;
    private int timesDefeated;

    public Statistics() {
        this(0, 0, 0, 0, 0, 0);
    }

    public Statistics(int missionsAttempted,
                      int missionsWon,
                      int missionsLost,
                      int trainingSessions,
                      int totalDamageDealt,
                      int timesDefeated) {
        this.missionsAttempted = missionsAttempted;
        this.missionsWon = missionsWon;
        this.missionsLost = missionsLost;
        this.trainingSessions = trainingSessions;
        this.totalDamageDealt = totalDamageDealt;
        this.timesDefeated = timesDefeated;
    }

    public void recordMissionAttempt() {
        missionsAttempted++;
    }

    public void recordMissionWin() {
        missionsWon++;
    }

    public void recordMissionLoss() {
        missionsLost++;
    }

    public void recordTraining() {
        trainingSessions++;
    }

    public void addDamageDealt(int damage) {
        totalDamageDealt += Math.max(0, damage);
    }

    public void recordDefeat() {
        timesDefeated++;
    }

    public int getMissionsAttempted() {
        return missionsAttempted;
    }

    public int getMissionsWon() {
        return missionsWon;
    }

    public int getMissionsLost() {
        return missionsLost;
    }

    public int getTrainingSessions() {
        return trainingSessions;
    }

    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public int getTimesDefeated() {
        return timesDefeated;
    }
}