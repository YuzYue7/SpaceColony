package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.data.AppRepository;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.manager.CrewFactory;
import com.lssl.spacecolony.manager.SaveLoadManager;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.enums.Specialization;

import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvSummary;
    private Storage storage;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        tvSummary = view.findViewById(R.id.tvSummary);
        Button btnCreateDemoCrew = view.findViewById(R.id.btnCreateDemoCrew);
        Button btnRefresh = view.findViewById(R.id.btnRefresh);
        Button btnSaveGame = view.findViewById(R.id.btnSaveGame);
        Button btnLoadGame = view.findViewById(R.id.btnLoadGame);
        Button btnGoRecruit = view.findViewById(R.id.btnGoRecruit);
        Button btnGoCrewList = view.findViewById(R.id.btnGoCrewList);
        Button btnGoQuarters = view.findViewById(R.id.btnGoQuarters);
        Button btnGoSimulator = view.findViewById(R.id.btnGoSimulator);
        Button btnGoMissionControl = view.findViewById(R.id.btnGoMissionControl);
        Button btnGoStatistics = view.findViewById(R.id.btnGoStatistics);

        btnCreateDemoCrew.setOnClickListener(v -> {
            createDemoCrewIfNeeded();
            updateSummary();
        });

        btnRefresh.setOnClickListener(v -> updateSummary());

        btnSaveGame.setOnClickListener(v -> {
            SaveLoadManager.saveGame(requireContext(), storage);
            Toast.makeText(requireContext(), "Game saved.", Toast.LENGTH_SHORT).show();
        });

        btnLoadGame.setOnClickListener(v -> {
            boolean success = SaveLoadManager.loadGame(requireContext(), storage);
            if (success) {
                updateSummary();
                Toast.makeText(requireContext(), "Game loaded.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No saved game found.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoRecruit.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new RecruitFragment())
                        .commit()
        );

        btnGoCrewList.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new CrewListFragment())
                        .commit()
        );

        btnGoQuarters.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new QuartersFragment())
                        .commit()
        );

        btnGoSimulator.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new SimulatorFragment())
                        .commit()
        );

        btnGoMissionControl.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new MissionControlFragment())
                        .commit()
        );

        btnGoStatistics.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new StatisticsFragment())
                        .commit()
        );

        updateSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummary();
    }

    private void createDemoCrewIfNeeded() {
        int addedCount = 0;

        if (!storage.containsCrewName("Nova")) {
            storage.add(CrewFactory.createCrewMember("Nova", Specialization.PILOT));
            addedCount++;
        }

        if (!storage.containsCrewName("Bolt")) {
            storage.add(CrewFactory.createCrewMember("Bolt", Specialization.ENGINEER));
            addedCount++;
        }

        if (!storage.containsCrewName("Luna")) {
            storage.add(CrewFactory.createCrewMember("Luna", Specialization.MEDIC));
            addedCount++;
        }

        if (!storage.containsCrewName("Orion")) {
            storage.add(CrewFactory.createCrewMember("Orion", Specialization.SCIENTIST));
            addedCount++;
        }

        if (!storage.containsCrewName("Rex")) {
            storage.add(CrewFactory.createCrewMember("Rex", Specialization.SOLDIER));
            addedCount++;
        }

        if (addedCount == 0) {
            Toast.makeText(requireContext(), "All demo crew already exist.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    addedCount + " demo crew member(s) added.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSummary() {
        if (tvSummary == null || storage == null) {
            return;
        }

        List<CrewMember> allCrew = storage.getAll();
        int total = allCrew.size();
        int quarters = storage.getByLocation(Location.QUARTERS).size();
        int simulator = storage.getByLocation(Location.SIMULATOR).size();
        int missionControl = storage.getByLocation(Location.MISSION_CONTROL).size();
        int medbay = storage.getByLocation(Location.MEDBAY).size();

        String summary = "Total crew members: " + total + "\n"
                + "Quarters: " + quarters + "\n"
                + "Simulator: " + simulator + "\n"
                + "Mission Control: " + missionControl + "\n"
                + "Medbay: " + medbay + "\n\n"
                + "Manage your colony from the sections below.";

        tvSummary.setText(summary);
    }
}