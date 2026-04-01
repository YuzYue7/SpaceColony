package com.lssl.spacecolony.model.crew;

import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.mission.Threat;

public class Engineer extends CrewMember {

    public Engineer(String name) {
        super(name, Specialization.ENGINEER, 6, 3, 19);
    }

    @Override
    public int getMissionBonus(MissionType missionType) {
        return missionType == MissionType.REPAIR ? 2 : 0;
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, MissionType missionType) {
        int bonus = missionType == MissionType.REPAIR ? 5 : 3;
        int rawDamage = getEffectiveSkill(missionType) + bonus;
        return threat.defend(rawDamage);
    }
}