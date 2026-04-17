package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.model.crew.CrewMember;

import java.util.ArrayList;
import java.util.List;

public class MissionResultFragment extends Fragment {

    private static final String ARG_SUCCESS = "arg_success";
    private static final String ARG_THREAT_NAME = "arg_threat_name";
    private static final String ARG_MISSION_TYPE = "arg_mission_type";
    private static final String ARG_SURVIVORS = "arg_survivors";
    private static final String ARG_DEFEATED = "arg_defeated";
    private static final String ARG_LOG_SUMMARY = "arg_log_summary";

    public MissionResultFragment() {
    }

    public static MissionResultFragment newInstance(
            boolean success,
            String threatName,
            String missionType,
            String survivorsText,
            String defeatedText,
            String logSummary
    ) {
        MissionResultFragment fragment = new MissionResultFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SUCCESS, success);
        args.putString(ARG_THREAT_NAME, threatName);
        args.putString(ARG_MISSION_TYPE, missionType);
        args.putString(ARG_SURVIVORS, survivorsText);
        args.putString(ARG_DEFEATED, defeatedText);
        args.putString(ARG_LOG_SUMMARY, logSummary);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mission_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvResultStatus = view.findViewById(R.id.tvResultStatus);
        TextView tvThreatSummary = view.findViewById(R.id.tvThreatSummary);
        TextView tvSurvivors = view.findViewById(R.id.tvSurvivors);
        TextView tvDefeated = view.findViewById(R.id.tvDefeated);
        TextView tvLogSummary = view.findViewById(R.id.tvLogSummary);
        Button btnBackToMissionControl = view.findViewById(R.id.btnBackToMissionControl);
        Button btnBackHome = view.findViewById(R.id.btnBackHomeFromResult);

        Bundle args = getArguments();
        boolean success = args != null && args.getBoolean(ARG_SUCCESS, false);
        String threatName = args != null ? args.getString(ARG_THREAT_NAME, "Unknown Threat") : "Unknown Threat";
        String missionType = args != null ? args.getString(ARG_MISSION_TYPE, "Unknown") : "Unknown";
        String survivorsText = args != null ? args.getString(ARG_SURVIVORS, "None") : "None";
        String defeatedText = args != null ? args.getString(ARG_DEFEATED, "None") : "None";
        String logSummary = args != null ? args.getString(ARG_LOG_SUMMARY, "No summary available.") : "No summary available.";

        tvResultStatus.setText(success ? "VICTORY" : "DEFEAT");
        tvResultStatus.setTextColor(getResources().getColor(
                success ? R.color.sc_success : R.color.sc_danger,
                null
        ));

        tvThreatSummary.setText("Mission Type: " + missionType + "\nThreat: " + threatName);
        tvSurvivors.setText(survivorsText);
        tvDefeated.setText(defeatedText);
        tvLogSummary.setText(logSummary);

        btnBackToMissionControl.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new MissionControlFragment())
                        .commit()
        );

        btnBackHome.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit()
        );
    }

    public static String buildCrewSummary(List<CrewMember> crewList, boolean includeExpReward) {
        if (crewList == null || crewList.isEmpty()) {
            return "None";
        }

        List<String> lines = new ArrayList<>();
        for (CrewMember crewMember : crewList) {
            String line = crewMember.getName()
                    + " • " + crewMember.getSpecialization()
                    + " • Energy " + crewMember.getEnergy() + "/" + crewMember.getMaxEnergy();

            if (includeExpReward) {
                line += " • +1 EXP if survived";
            }
            lines.add(line);
        }
        return TextUtils.join("\n", lines);
    }

    public static String buildLogSummary(List<String> logs) {
        if (logs == null || logs.isEmpty()) {
            return "No log available.";
        }

        int start = Math.max(0, logs.size() - 8);
        List<String> summaryLines = logs.subList(start, logs.size());
        return TextUtils.join("\n", summaryLines);
    }
}