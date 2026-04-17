package com.lssl.spacecolony.model.mission;

import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.ActionType;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.enums.MissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mission {
    private final List<CrewMember> crewList;
    private final Threat threat;
    private final MissionType missionType;
    private final List<String> logs;
    private final Random random;

    private int currentActorIndex;
    private int round;
    private boolean missionStarted;
    private boolean missionFinished;

    public Mission(List<CrewMember> crewList, Threat threat, MissionType missionType) {
        this.crewList = crewList;
        this.threat = threat;
        this.missionType = missionType;
        this.logs = new ArrayList<>();
        this.random = new Random();
        this.currentActorIndex = 0;
        this.round = 1;
        this.missionStarted = false;
        this.missionFinished = false;
    }

    public void startMission() {
        if (missionStarted) {
            return;
        }

        missionStarted = true;
        logs.add("=== MISSION START ===");
        logs.add("Mission Type: " + missionType);
        logs.add("Threat: " + threat.getName()
                + " | skill=" + threat.getSkill()
                + " | res=" + threat.getResilience()
                + " | energy=" + threat.getEnergy() + "/" + threat.getMaxEnergy());

        for (CrewMember crewMember : crewList) {
            logs.add("Crew: " + crewMember);
            crewMember.recordMissionAttempt();
        }

        logs.add("--- Round " + round + " ---");
        moveToNextAliveActorIfNeeded();
    }

    public List<String> performTurn(ActionType actionType) {
        List<String> stepLogs = new ArrayList<>();

        if (missionFinished || !missionStarted) {
            return stepLogs;
        }

        CrewMember actor = getCurrentActor();
        if (actor == null) {
            missionFinished = true;
            recordMissionFailure();
            moveDefeatedCrewToMedbay();
            logs.add("Mission failed. All crew members lost.");
            stepLogs.add("Mission failed. All crew members lost.");
            return stepLogs;
        }

        CrewMember ally = getAllyOf(actor);
        int damage = actor.performAction(actionType, threat, ally, missionType);

        if (actionType == ActionType.DEFEND) {
            String msg = actor.getName() + " uses DEFEND.";
            logs.add(msg);
            stepLogs.add(msg);
        } else if (actionType == ActionType.SPECIAL) {
            String msg = actor.getName() + " uses SPECIAL.";
            logs.add(msg);
            stepLogs.add(msg);

            if (damage > 0) {
                String dmgMsg = "Special damage dealt: " + damage;
                logs.add(dmgMsg);
                stepLogs.add(dmgMsg);
            } else {
                String infoMsg = "Special action completed.";
                logs.add(infoMsg);
                stepLogs.add(infoMsg);
            }
        } else {
            String msg = actor.getName() + " attacks.";
            logs.add(msg);
            stepLogs.add(msg);

            String dmgMsg = "Damage dealt: " + damage;
            logs.add(dmgMsg);
            stepLogs.add(dmgMsg);
        }

        String threatMsg = "Threat energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy();
        logs.add(threatMsg);
        stepLogs.add(threatMsg);

        if (threat.isDefeated()) {
            missionFinished = true;
            logs.add("The threat has been neutralized!");
            stepLogs.add("The threat has been neutralized!");
            rewardSurvivors();
            moveDefeatedCrewToMedbay();
            moveSurvivorsToMissionControl();
            return stepLogs;
        }

        CrewMember target = threat.chooseTarget(crewList, random);
        if (target != null) {
            int taken = threat.attack(target);

            String retaliateMsg = threat.getName() + " retaliates against "
                    + target.getName() + " for " + taken + " damage.";
            logs.add(retaliateMsg);
            stepLogs.add(retaliateMsg);

            String energyMsg = target.getName() + " energy: "
                    + target.getEnergy() + "/" + target.getMaxEnergy();
            logs.add(energyMsg);
            stepLogs.add(energyMsg);

            if (!target.isAlive()) {
                target.setLocation(Location.MEDBAY);

                String defeatMsg = target.getName() + " has been defeated and moved to Medbay.";
                logs.add(defeatMsg);
                stepLogs.add(defeatMsg);
            }
        }

        if (!hasAliveCrew()) {
            missionFinished = true;
            recordMissionFailure();
            moveDefeatedCrewToMedbay();
            logs.add("Mission failed. All crew members lost.");
            stepLogs.add("Mission failed. All crew members lost.");
            return stepLogs;
        }

        advanceToNextActor();
        return stepLogs;
    }

    private void advanceToNextActor() {
        int checked = 0;
        int size = crewList.size();

        do {
            currentActorIndex = (currentActorIndex + 1) % size;
            checked++;
        } while (checked <= size && !crewList.get(currentActorIndex).isAlive());

        if (currentActorIndex == 0 || allPreviousDeadBeforeIndex()) {
            round++;
            String roundMsg = "--- Round " + round + " ---";
            logs.add(roundMsg);
        }

        moveToNextAliveActorIfNeeded();
    }

    private boolean allPreviousDeadBeforeIndex() {
        for (int i = 0; i < currentActorIndex; i++) {
            if (crewList.get(i).isAlive()) {
                return false;
            }
        }
        return true;
    }

    private void moveToNextAliveActorIfNeeded() {
        if (getCurrentActor() != null && getCurrentActor().isAlive()) {
            return;
        }

        for (int i = 0; i < crewList.size(); i++) {
            if (crewList.get(i).isAlive()) {
                currentActorIndex = i;
                return;
            }
        }
    }

    public CrewMember getCurrentActor() {
        if (crewList.isEmpty()) {
            return null;
        }
        CrewMember crewMember = crewList.get(currentActorIndex);
        return crewMember.isAlive() ? crewMember : null;
    }

    public Threat getThreat() {
        return threat;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public boolean isMissionFinished() {
        return missionFinished;
    }

    public boolean isMissionSuccess() {
        return missionFinished && threat.isDefeated();
    }

    public List<String> getLogs() {
        return logs;
    }

    public List<CrewMember> getCrewList() {
        return crewList;
    }

    public List<CrewMember> getSurvivors() {
        List<CrewMember> survivors = new ArrayList<>();
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember.isAlive()) {
                survivors.add(crewMember);
            }
        }
        return survivors;
    }

    public List<CrewMember> getDefeatedCrew() {
        List<CrewMember> defeated = new ArrayList<>();
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && !crewMember.isAlive()) {
                defeated.add(crewMember);
            }
        }
        return defeated;
    }

    private CrewMember getAllyOf(CrewMember current) {
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember != current && crewMember.isAlive()) {
                return crewMember;
            }
        }
        return null;
    }

    private boolean hasAliveCrew() {
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private void rewardSurvivors() {
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember.isAlive()) {
                crewMember.addExperience(1);
                crewMember.recordMissionWin();
            } else if (crewMember != null) {
                crewMember.recordMissionLoss();
            }
        }
    }

    private void recordMissionFailure() {
        for (CrewMember crewMember : crewList) {
            if (crewMember != null) {
                crewMember.recordMissionLoss();
            }
        }
    }

    private void moveSurvivorsToMissionControl() {
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember.isAlive()) {
                crewMember.setLocation(Location.MISSION_CONTROL);
            }
        }
    }

    private void moveDefeatedCrewToMedbay() {
        for (CrewMember crewMember : crewList) {
            if (crewMember != null && !crewMember.isAlive()) {
                crewMember.setLocation(Location.MEDBAY);
            }
        }
    }
}