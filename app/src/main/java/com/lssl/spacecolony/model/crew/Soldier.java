package com.lssl.spacecolony.model.crew;

import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.mission.Threat;

public class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name, Specialization.SOLDIER, 9, 0, 16);
    }

    @Override
    public int getMissionBonus(MissionType missionType) {
        return missionType == MissionType.COMBAT ? 2 : 0;
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, MissionType missionType) {
        int rawDamage = getEffectiveSkill(missionType) + 6;
        return threat.defend(rawDamage);
    }
}