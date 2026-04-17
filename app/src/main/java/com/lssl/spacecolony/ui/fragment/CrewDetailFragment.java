package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.data.AppRepository;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.enums.Specialization;
import com.lssl.spacecolony.model.stats.Statistics;

public class CrewDetailFragment extends Fragment {

    private static final String ARG_CREW_ID = "arg_crew_id";

    private Storage storage;
    private CrewMember crewMember;

    private ImageView ivCrewSpecialization;
    private TextView tvCrewName;
    private TextView tvCrewSpecialization;
    private TextView tvCrewLocation;
    private ProgressBar progressEnergy;
    private TextView tvEnergyText;
    private TextView tvCoreStats;
    private TextView tvPerformanceStats;
    private EditText etRenameCrewDetail;

    public CrewDetailFragment() {
    }

    public static CrewDetailFragment newInstance(int crewId) {
        CrewDetailFragment fragment = new CrewDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CREW_ID, crewId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crew_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        ivCrewSpecialization = view.findViewById(R.id.ivCrewSpecialization);
        tvCrewName = view.findViewById(R.id.tvCrewName);
        tvCrewSpecialization = view.findViewById(R.id.tvCrewSpecialization);
        tvCrewLocation = view.findViewById(R.id.tvCrewLocation);
        progressEnergy = view.findViewById(R.id.progressEnergy);
        tvEnergyText = view.findViewById(R.id.tvEnergyText);
        tvCoreStats = view.findViewById(R.id.tvCoreStats);
        tvPerformanceStats = view.findViewById(R.id.tvPerformanceStats);
        etRenameCrewDetail = view.findViewById(R.id.etRenameCrewDetail);

        Button btnRenameFromDetail = view.findViewById(R.id.btnRenameFromDetail);
        Button btnBackToCrewList = view.findViewById(R.id.btnBackToCrewList);

        int crewId = getArguments() != null ? getArguments().getInt(ARG_CREW_ID, -1) : -1;
        crewMember = storage.get(crewId);

        if (crewMember == null) {
            Toast.makeText(requireContext(), "Crew member not found.", Toast.LENGTH_SHORT).show();
            goBackToCrewList();
            return;
        }

        renderCrewDetail();

        btnRenameFromDetail.setOnClickListener(v -> renameCrewFromDetail());
        btnBackToCrewList.setOnClickListener(v -> goBackToCrewList());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (crewMember != null) {
            crewMember = storage.get(crewMember.getId());
            if (crewMember != null) {
                renderCrewDetail();
            }
        }
    }

    private void renderCrewDetail() {
        tvCrewName.setText(crewMember.getName());
        tvCrewSpecialization.setText(crewMember.getSpecialization().name());
        tvCrewSpecialization.setTextColor(getSpecColor(crewMember.getSpecialization()));
        tvCrewLocation.setText(formatLocation(crewMember.getLocation()));

        bindSpecializationIcon(crewMember.getSpecialization());

        progressEnergy.setMax(Math.max(1, crewMember.getMaxEnergy()));
        progressEnergy.setProgress(Math.max(0, crewMember.getEnergy()));
        tvEnergyText.setText("Energy " + crewMember.getEnergy() + "/" + crewMember.getMaxEnergy());

        int effectiveSkill = crewMember.getBaseSkill() + crewMember.getExperience();

        tvCoreStats.setText(
                "Skill: " + crewMember.getBaseSkill() + "\n"
                        + "Resilience: " + crewMember.getResilience() + "\n"
                        + "Experience: " + crewMember.getExperience() + "\n"
                        + "Estimated Effective Skill: " + effectiveSkill + "\n"
                        + "Alive: " + (crewMember.isAlive() ? "Yes" : "No")
        );

        Statistics stats = crewMember.getStatistics();
        tvPerformanceStats.setText(
                "Mission Attempts: " + stats.getMissionsAttempted() + "\n"
                        + "Wins: " + stats.getMissionsWon() + "\n"
                        + "Losses: " + stats.getMissionsLost() + "\n"
                        + "Training Sessions: " + stats.getTrainingSessions() + "\n"
                        + "Damage Dealt: " + stats.getTotalDamageDealt() + "\n"
                        + "Times Defeated: " + stats.getTimesDefeated()
        );

        etRenameCrewDetail.setText("");
    }

    private void renameCrewFromDetail() {
        String newName = etRenameCrewDetail.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(requireContext(), "Please enter a new name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (storage.containsCrewNameExceptId(newName, crewMember.getId())) {
            Toast.makeText(requireContext(), "Another crew member already has this name.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = storage.renameCrewMember(crewMember.getId(), newName);
        if (success) {
            crewMember = storage.get(crewMember.getId());
            renderCrewDetail();
            Toast.makeText(requireContext(), "Crew member renamed successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Rename failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackToCrewList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new CrewListFragment())
                .commit();
    }

    private void bindSpecializationIcon(Specialization specialization) {
        switch (specialization) {
            case PILOT:
                ivCrewSpecialization.setImageResource(R.drawable.ic_pilot);
                break;
            case ENGINEER:
                ivCrewSpecialization.setImageResource(R.drawable.ic_engineer);
                break;
            case MEDIC:
                ivCrewSpecialization.setImageResource(R.drawable.ic_medic);
                break;
            case SCIENTIST:
                ivCrewSpecialization.setImageResource(R.drawable.ic_scientist);
                break;
            case SOLDIER:
                ivCrewSpecialization.setImageResource(R.drawable.ic_soldier);
                break;
        }
    }

    private int getSpecColor(Specialization specialization) {
        int colorRes;
        switch (specialization) {
            case PILOT:
                colorRes = R.color.sc_pilot;
                break;
            case ENGINEER:
                colorRes = R.color.sc_engineer;
                break;
            case MEDIC:
                colorRes = R.color.sc_medic;
                break;
            case SCIENTIST:
                colorRes = R.color.sc_scientist;
                break;
            default:
                colorRes = R.color.sc_soldier;
                break;
        }
        return ContextCompat.getColor(requireContext(), colorRes);
    }

    private String formatLocation(Location location) {
        switch (location) {
            case QUARTERS:
                return "QUARTERS";
            case SIMULATOR:
                return "SIMULATOR";
            case MISSION_CONTROL:
                return "MISSION";
            case MEDBAY:
                return "MEDBAY";
            default:
                return location.name();
        }
    }
}