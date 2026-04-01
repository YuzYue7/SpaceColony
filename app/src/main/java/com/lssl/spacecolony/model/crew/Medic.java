package com.lssl.spacecolony.model.crew;

import com.lssl.spacecolony.model.enums.MissionType;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.mission.Threat;

public class Medic extends CrewMember {

    public Medic(String name) {
        super(name, Specialization.MEDIC, 7, 2, 18);
    }

    @Override
    public int getMissionBonus(MissionType missionType) {
        return missionType == MissionType.MEDICAL ? 2 : 0;
    }

    @Override
    public int useSpecialAbility(Threat threat, CrewMember ally, MissionType missionType) {
        if (ally != null && ally.isAlive() && ally.getEnergy() < ally.getMaxEnergy()) {
            ally.heal(5);
            return 0;
        }
        return threat.defend(getEffectiveSkill(missionType));
    }
}