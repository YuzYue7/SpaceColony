package com.lssl.spacecolony.model.crew;

import com.lssl.spacecolony.model.enums.ActionType;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.mission.Threat;
import com.lssl.spacecolony.model.stats.Statistics;

public abstract class CrewMember {
    private static int idCounter = 1;

    private final int id;
    private String name;
    private final Specialization specialization;

    private int baseSkill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;

    private Location location;
    private boolean defending;
    private boolean alive;

    private Statistics statistics;

    public CrewMember(String name,
                      Specialization specialization,
                      int baseSkill,
                      int resilience,
                      int maxEnergy) {
        this.id = idCounter++;
        this.name = name;
        this.specialization = specialization;
        this.baseSkill = baseSkill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
        this.location = Location.QUARTERS;
        this.defending = false;
        this.alive = true;
        this.statistics = new Statistics();
    }

    public int getEffectiveSkill(MissionType missionType) {
        return baseSkill + experience + getMissionBonus(missionType);
    }

    public int getMissionBonus(MissionType missionType) {
        return 0;
    }

    public int attack(Threat threat, MissionType missionType) {
        int rawDamage = getEffectiveSkill(missionType);
        int dealtDamage = threat.defend(rawDamage);
        statistics.addDamageDealt(dealtDamage);
        return dealtDamage;
    }

    public int performAction(ActionType actionType, Threat threat, CrewMember ally, MissionType missionType) {
        defending = false;

        switch (actionType) {
            case ATTACK:
                return attack(threat, missionType);
            case DEFEND:
                defending = true;
                return 0;
            case SPECIAL:
                return useSpecialAbility(threat, ally, missionType);
            default:
                return 0;
        }
    }

    public int defend(int incomingDamage) {
        int actualResilience = resilience;
        if (defending) {
            actualResilience += 2;
        }

        int finalDamage = Math.max(1, incomingDamage - actualResilience);
        energy -= finalDamage;

        if (energy <= 0) {
            energy = 0;
            alive = false;
            statistics.recordDefeat();
        }

        return finalDamage;
    }

    public abstract int useSpecialAbility(Threat threat, CrewMember ally, MissionType missionType);

    public void train() {
        experience++;
        statistics.recordTraining();
    }

    public void restoreEnergy() {
        energy = maxEnergy;
        defending = false;
    }

    public void heal(int amount) {
        if (amount <= 0 || !alive) {
            return;
        }
        energy = Math.min(maxEnergy, energy + amount);
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void reviveWithPenalty() {
        alive = true;
        energy = Math.max(1, maxEnergy / 2);
        experience = Math.max(0, experience - 1);
        defending = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public int getBaseSkill() {
        return baseSkill;
    }

    public int getResilience() {
        return resilience;
    }

    public int getExperience() {
        return experience;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public Location getLocation() {
        return location;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setBaseSkill(int baseSkill) {
        this.baseSkill = baseSkill;
    }

    public void setResilience(int resilience) {
        this.resilience = resilience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public void setStatistics(Statistics statistics) {
        if (statistics != null) {
            this.statistics = statistics;
        }
    }

    public void addExperience(int amount) {
        if (amount > 0) {
            experience += amount;
        }
    }

    public void recordMissionAttempt() {
        statistics.recordMissionAttempt();
    }

    public void recordMissionWin() {
        statistics.recordMissionWin();
    }

    public void recordMissionLoss() {
        statistics.recordMissionLoss();
    }

    @Override
    public String toString() {
        return specialization + "(" + name + ") "
                + "skill=" + baseSkill
                + ", exp=" + experience
                + ", res=" + resilience
                + ", energy=" + energy + "/" + maxEnergy
                + ", location=" + location;
    }
}