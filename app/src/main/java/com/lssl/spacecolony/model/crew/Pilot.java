package com.lssl.spacecolony.model.crew;

import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.mission.Threat;

public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, Specialization.PILOT, 5, 4, 20);
    }

    @Override
    public int getMissionBonus(MissionType missionType) {
        return missionType == MissionType.NAVIGATION ? 2 : 0;
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, MissionType missionType) {
        setDefending(true);
        int rawDamage = getEffectiveSkill(missionType) + 2;
        return threat.defend(rawDamage);
    }
}