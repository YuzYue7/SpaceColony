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
        String normalized = normalizeName(name);
        if (normalized.isEmpty()) {
            return false;
        }

        for (CrewMember crewMember : crewMap.values()) {
            if (normalizeName(crewMember.getName()).equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsCrewNameExceptId(String name, int excludedId) {
        String normalized = normalizeName(name);
        if (normalized.isEmpty()) {
            return false;
        }

        for (CrewMember crewMember : crewMap.values()) {
            if (crewMember.getId() == excludedId) {
                continue;
            }

            if (normalizeName(crewMember.getName()).equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }

    public boolean renameCrewMember(int id, String newName) {
        CrewMember crewMember = crewMap.get(id);
        String normalized = normalizeName(newName);

        if (crewMember == null || normalized.isEmpty()) {
            return false;
        }

        if (containsCrewNameExceptId(normalized, id)) {
            return false;
        }

        crewMember.setName(normalized);
        return true;
    }

    public void moveDefeatedCrewToMedbay() {
        for (CrewMember crewMember : crewMap.values()) {
            if (!crewMember.isAlive()) {
                crewMember.setLocation(Location.MEDBAY);
            }
        }
    }

    public void recoverCrewFromMedbay(int id) {
        CrewMember crewMember = crewMap.get(id);
        if (crewMember != null && crewMember.getLocation() == Location.MEDBAY) {
            crewMember.reviveWithPenalty();
            crewMember.setLocation(Location.QUARTERS);
        }
    }

    public void clear() {
        crewMap.clear();
    }

    public int size() {
        return crewMap.size();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}