package com.lssl.spacecolony.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.lssl.spacecolony.data.CrewSaveData;
import com.lssl.spacecolony.data.GameState;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.stats.Statistics;

import java.util.List;

public class SaveLoadManager {

    private static final String PREFS_NAME = "space_colony_prefs";
    private static final String KEY_GAME_STATE = "game_state";

    public static void saveGame(Context context, Storage storage) {
        GameState gameState = new GameState();
        List<CrewMember> crewMembers = storage.getAll();

        for (CrewMember crewMember : crewMembers) {
            CrewSaveData saveData = new CrewSaveData();
            saveData.name = crewMember.getName();
            saveData.specialization = crewMember.getSpecialization().name();
            saveData.baseSkill = crewMember.getBaseSkill();
            saveData.resilience = crewMember.getResilience();
            saveData.experience = crewMember.getExperience();
            saveData.energy = crewMember.getEnergy();
            saveData.maxEnergy = crewMember.getMaxEnergy();
            saveData.location = crewMember.getLocation().name();
            saveData.alive = crewMember.isAlive();

            Statistics stats = crewMember.getStatistics();
            saveData.missionsAttempted = stats.getMissionsAttempted();
            saveData.missionsWon = stats.getMissionsWon();
            saveData.missionsLost = stats.getMissionsLost();
            saveData.trainingSessions = stats.getTrainingSessions();
            saveData.totalDamageDealt = stats.getTotalDamageDealt();
            saveData.timesDefeated = stats.getTimesDefeated();

            gameState.crewList.add(saveData);
        }

        gameState.completedMissionCount = ThreatFactory.getCompletedMissionCount();

        String json = new Gson().toJson(gameState);

        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_GAME_STATE, json).apply();
    }

    public static boolean loadGame(Context context, Storage storage) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(KEY_GAME_STATE, null);

        if (json == null || json.isEmpty()) {
            return false;
        }

        GameState gameState = new Gson().fromJson(json, GameState.class);
        if (gameState == null) {
            return false;
        }

        storage.clear();

        for (CrewSaveData saveData : gameState.crewList) {
            CrewMember crewMember = CrewFactory.createCrewMember(
                    saveData.name,
                    Specialization.valueOf(saveData.specialization)
            );

            crewMember.setBaseSkill(saveData.baseSkill);
            crewMember.setResilience(saveData.resilience);
            crewMember.setExperience(saveData.experience);
            crewMember.setEnergy(saveData.energy);
            crewMember.setMaxEnergy(saveData.maxEnergy);
            crewMember.setLocation(Location.valueOf(saveData.location));
            crewMember.setAlive(saveData.alive);

            Statistics restoredStats = new Statistics(
                    saveData.missionsAttempted,
                    saveData.missionsWon,
                    saveData.missionsLost,
                    saveData.trainingSessions,
                    saveData.totalDamageDealt,
                    saveData.timesDefeated
            );
            crewMember.setStatistics(restoredStats);

            storage.add(crewMember);
        }

        ThreatFactory.setCompletedMissionCount(gameState.completedMissionCount);
        return true;
    }
}