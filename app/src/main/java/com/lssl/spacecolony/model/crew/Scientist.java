package com.lssl.spacecolony.model.crew;

import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.mission.Threat;

public class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name, Specialization.SCIENTIST, 8, 1, 17);
    }

    @Override
    public int getMissionBonus(MissionType missionType) {
        return missionType == MissionType.RESEARCH ? 2 : 0;
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, MissionType missionType) {
        int bonus = missionType == MissionType.RESEARCH ? 6 : 4;
        int rawDamage = getEffectiveSkill(missionType) + bonus;
        return threat.defend(rawDamage);
    }
}