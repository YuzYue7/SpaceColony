package com.lssl.spacecolony.data;

import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
    private final HashMap<Integer, CrewMember> crewMap;

    public Storage() {
        this.crewMap = new HashMap<>();
    }

    public void add(CrewMember crewMember) {
        crewMap.put(crewMember.getId(), crewMember);
    }

    public CrewMember get(int id) {
        return crewMap.get(id);
    }

    public void remove(int id) {
        crewMap.remove(id);
    }

    public List<CrewMember> getAll() {
        return new ArrayList<>(crewMap.values());
    }

    public List<CrewMember> getByLocation(Location location) {
        List<CrewMember> result = new ArrayList<>();
        for (Map.Entry<Integer, CrewMember> entry : crewMap.entrySet()) {
            CrewMember crewMember = entry.getValue();
            if (crewMember.getLocation() == location) {
                result.add(crewMember);
            }
        }
        return result;
    }

    public void moveCrewMember(int id, Location newLocation) {
        CrewMember crewMember = crewMap.get(id);
        if (crewMember != null) {
            crewMember.setLocation(newLocation);
            if (newLocation == Location.QUARTERS) {
                crewMember.restoreEnergy();
            }
        }
    }

    public boolean containsCrewName(String name) {
        for (CrewMember crewMember : crewMap.values()) {
            if (crewMember.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void removeDeadCrew() {
        List<Integer> deadIds = new ArrayList<>();
        for (Map.Entry<Integer, CrewMember> entry : crewMap.entrySet()) {
            CrewMember crewMember = entry.getValue();
            if (!crewMember.isAlive()) {
                deadIds.add(entry.getKey());
            }
        }

        for (Integer id : deadIds) {
            crewMap.remove(id);
        }
    }

    public void clear() {
        crewMap.clear();
    }

    public int size() {
        return crewMap.size();
    }
}