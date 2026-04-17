package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.data.AppRepository;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.manager.ThreatFactory;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.ActionType;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.mission.Mission;
import com.lssl.spacecolony.model.mission.Threat;
import com.lssl.spacecolony.ui.adapter.SelectableCrewAdapter;

import java.util.List;

public class MissionControlFragment extends Fragment {

    private Storage storage;
    private SelectableCrewAdapter adapter;

    private TextView tvMissionLog;
    private TextView tvThreatInfo;
    private TextView tvCurrentActor;
    private LinearLayout layoutActionButtons;

    private Mission currentMission;

    public MissionControlFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mission_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerMissionCrew);
        Button btnLaunchMission = view.findViewById(R.id.btnLaunchMission);
        Button btnReturnToQuarters = view.findViewById(R.id.btnReturnToQuartersFromMission);
        Button btnBack = view.findViewById(R.id.btnBackHomeFromMission);
        Button btnAttack = view.findViewById(R.id.btnAttack);
        Button btnDefend = view.findViewById(R.id.btnDefend);
        Button btnSpecial = view.findViewById(R.id.btnSpecial);

        tvMissionLog = view.findViewById(R.id.tvMissionLog);
        tvThreatInfo = view.findViewById(R.id.tvThreatInfo);
        tvCurrentActor = view.findViewById(R.id.tvCurrentActor);
        layoutActionButtons = view.findViewById(R.id.layoutActionButtons);

        adapter = new SelectableCrewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        refreshList();
        updateMissionUi();

        btnLaunchMission.setOnClickListener(v -> launchMission());

        btnReturnToQuarters.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelectedCrew();
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one crew member.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CrewMember crewMember : selected) {
                storage.moveCrewMember(crewMember.getId(), Location.QUARTERS);
            }

            Toast.makeText(requireContext(), "Selected crew returned to Quarters.", Toast.LENGTH_SHORT).show();
            refreshList();
        });

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit()
        );

        btnAttack.setOnClickListener(v -> performPlayerAction(ActionType.ATTACK));
        btnDefend.setOnClickListener(v -> performPlayerAction(ActionType.DEFEND));
        btnSpecial.setOnClickListener(v -> performPlayerAction(ActionType.SPECIAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        updateMissionUi();
    }

    private void launchMission() {
        List<CrewMember> selected = adapter.getSelectedCrew();

        if (selected.size() < 2) {
            Toast.makeText(requireContext(), "Select at least 2 crew members.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selected.size() > 3) {
            Toast.makeText(requireContext(), "Select at most 3 crew members.", Toast.LENGTH_SHORT).show();
            return;
        }

        Threat threat = ThreatFactory.createThreat();
        currentMission = new Mission(selected, threat, threat.getMissionType());
        currentMission.startMission();

        tvMissionLog.setText(TextUtils.join("\n", currentMission.getLogs()));
        updateMissionUi();

        Toast.makeText(requireContext(), "Mission started.", Toast.LENGTH_SHORT).show();
    }

    private void performPlayerAction(ActionType actionType) {
        if (currentMission == null || currentMission.isMissionFinished()) {
            Toast.makeText(requireContext(), "No active mission.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentMission.performTurn(actionType);
        tvMissionLog.setText(TextUtils.join("\n", currentMission.getLogs()));

        if (currentMission.isMissionFinished()) {
            if (currentMission.isMissionSuccess()) {
                ThreatFactory.recordMissionSuccess();
            }

            storage.moveDefeatedCrewToMedbay();
            refreshList();
            updateMissionUi();
            openMissionResultScreen();
            return;
        }

        updateMissionUi();
    }

    private void openMissionResultScreen() {
        if (currentMission == null) {
            return;
        }

        String survivorsText = MissionResultFragment.buildCrewSummary(
                currentMission.getSurvivors(),
                currentMission.isMissionSuccess()
        );

        String defeatedText = MissionResultFragment.buildCrewSummary(
                currentMission.getDefeatedCrew(),
                false
        );

        String logSummary = MissionResultFragment.buildLogSummary(currentMission.getLogs());

        MissionResultFragment resultFragment = MissionResultFragment.newInstance(
                currentMission.isMissionSuccess(),
                currentMission.getThreat().getName(),
                currentMission.getMissionType().name(),
                survivorsText,
                defeatedText,
                logSummary
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, resultFragment)
                .commit();
    }

    private void updateMissionUi() {
        if (currentMission == null) {
            tvThreatInfo.setText("Threat info will appear here");
            tvCurrentActor.setText("Current actor will appear here");
            layoutActionButtons.setVisibility(View.GONE);
            return;
        }

        Threat threat = currentMission.getThreat();
        tvThreatInfo.setText("Threat: " + threat.getName()
                + " | Type: " + threat.getMissionType()
                + " | Energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy());

        CrewMember actor = currentMission.getCurrentActor();
        if (actor != null && !currentMission.isMissionFinished()) {
            tvCurrentActor.setText("Current Actor: " + actor.getName()
                    + " (" + actor.getSpecialization() + ")"
                    + " | Energy: " + actor.getEnergy() + "/" + actor.getMaxEnergy());
            layoutActionButtons.setVisibility(View.VISIBLE);
        } else {
            tvCurrentActor.setText(currentMission.isMissionFinished()
                    ? "Mission finished."
                    : "No active actor.");
            layoutActionButtons.setVisibility(View.GONE);
        }
    }

    private void refreshList() {
        if (adapter != null && storage != null) {
            adapter.setCrewList(storage.getByLocation(Location.MISSION_CONTROL));
        }
    }
}