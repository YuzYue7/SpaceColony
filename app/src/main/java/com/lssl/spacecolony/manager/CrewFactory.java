package com.lssl.spacecolony.manager;

import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.crew.Engineer;
import com.lssl.spacecolony.model.crew.Medic;
import com.lssl.spacecolony.model.crew.Pilot;
import com.lssl.spacecolony.model.crew.Scientist;
import com.lssl.spacecolony.model.crew.Soldier;
import com.lssl.spacecolony.model.enums.Specialization;

public class CrewFactory {

    public static CrewMember createCrewMember(String name, Specialization specialization) {
        switch (specialization) {
            case PILOT:
                return new Pilot(name);
            case ENGINEER:
                return new Engineer(name);
            case MEDIC:
                return new Medic(name);
            case SCIENTIST:
                return new Scientist(name);
            case SOLDIER:
                return new Soldier(name);
            default:
                throw new IllegalArgumentException("Unknown specialization: " + specialization);
        }
    }
}