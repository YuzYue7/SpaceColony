package com.lssl.spacecolony.model.mission;

import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.MissionType;

import java.util.List;
import java.util.Random;

public class Threat {
    private final String name;
    private final MissionType missionType;
    private final int skill;
    private final int resilience;
    private int energy;
    private final int maxEnergy;

    public Threat(String name, MissionType missionType, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.missionType = missionType;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    public int attack(CrewMember target) {
        return target.defend(skill);
    }

    public int defend(int incomingDamage) {
        int finalDamage = Math.max(1, incomingDamage - resilience);
        energy -= finalDamage;
        if (energy < 0) {
            energy = 0;
        }
        return finalDamage;
    }

    public CrewMember chooseTarget(List<CrewMember> crewList, Random random) {
        CrewMember selected = null;
        int aliveCount = 0;

        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember.isAlive()) {
                aliveCount++;
            }
        }

        if (aliveCount == 0) {
            return null;
        }

        int index = random.nextInt(aliveCount);
        int current = 0;

        for (CrewMember crewMember : crewList) {
            if (crewMember != null && crewMember.isAlive()) {
                if (current == index) {
                    selected = crewMember;
                    break;
                }
                current++;
            }
        }

        return selected;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public String getName() {
        return name;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public int getSkill() {
        return skill;
    }

    public int getResilience() {
        return resilience;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }
}