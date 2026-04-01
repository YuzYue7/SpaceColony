package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.lssl.spacecolony.R;
import com.lssl.spacecolony.data.AppRepository;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.stats.Statistics;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private Storage storage;
    private TextView tvColonyStats;
    private TextView tvCrewStats;
    private BarChart barChartStats;
    private PieChart pieChartLocations;

    public StatisticsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        tvColonyStats = view.findViewById(R.id.tvColonyStats);
        tvCrewStats = view.findViewById(R.id.tvCrewStats);
        barChartStats = view.findViewById(R.id.barChartStats);
        pieChartLocations = view.findViewById(R.id.pieChartLocations);

        Button btnRefreshStatistics = view.findViewById(R.id.btnRefreshStatistics);
        Button btnBackHome = view.findViewById(R.id.btnBackHomeFromStatistics);

        btnRefreshStatistics.setOnClickListener(v -> updateStatistics());

        btnBackHome.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit()
        );

        setupBarChart();
        setupPieChart();
        updateStatistics();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatistics();
    }

    private void updateStatistics() {
        if (storage == null || tvColonyStats == null || tvCrewStats == null) {
            return;
        }

        List<CrewMember> allCrew = storage.getAll();

        int totalCrew = allCrew.size();
        int totalTrainings = 0;
        int totalMissionAttempts = 0;
        int totalMissionWins = 0;
        int totalMissionLosses = 0;
        int totalDamage = 0;
        int totalDefeats = 0;

        StringBuilder crewStatsBuilder = new StringBuilder();

        for (CrewMember crewMember : allCrew) {
            Statistics stats = crewMember.getStatistics();

            totalTrainings += stats.getTrainingSessions();
            totalMissionAttempts += stats.getMissionsAttempted();
            totalMissionWins += stats.getMissionsWon();
            totalMissionLosses += stats.getMissionsLost();
            totalDamage += stats.getTotalDamageDealt();
            totalDefeats += stats.getTimesDefeated();

            crewStatsBuilder
                    .append(crewMember.getName())
                    .append(" (").append(crewMember.getSpecialization()).append(")\n")
                    .append("Location: ").append(crewMember.getLocation()).append("\n")
                    .append("Exp: ").append(crewMember.getExperience()).append("\n")
                    .append("Missions Attempted: ").append(stats.getMissionsAttempted()).append("\n")
                    .append("Missions Won: ").append(stats.getMissionsWon()).append("\n")
                    .append("Missions Lost: ").append(stats.getMissionsLost()).append("\n")
                    .append("Training Sessions: ").append(stats.getTrainingSessions()).append("\n")
                    .append("Total Damage Dealt: ").append(stats.getTotalDamageDealt()).append("\n")
                    .append("Times Defeated: ").append(stats.getTimesDefeated()).append("\n")
                    .append("-------------------------\n");
        }

        String colonyStatsText = "Total Crew: " + totalCrew + "\n"
                + "Total Trainings: " + totalTrainings + "\n"
                + "Total Mission Attempts: " + totalMissionAttempts + "\n"
                + "Total Mission Wins: " + totalMissionWins + "\n"
                + "Total Mission Losses: " + totalMissionLosses + "\n"
                + "Total Damage Dealt: " + totalDamage + "\n"
                + "Total Defeats: " + totalDefeats;

        if (allCrew.isEmpty()) {
            crewStatsBuilder.append("No crew members available.");
        }

        tvColonyStats.setText(colonyStatsText);
        tvCrewStats.setText(crewStatsBuilder.toString());

        updateBarChart(totalTrainings, totalMissionWins, totalMissionLosses, totalDamage);
        updatePieChart();
    }

    private void setupBarChart() {
        Description description = new Description();
        description.setText("Colony performance");
        barChartStats.setDescription(description);

        XAxis xAxis = barChartStats.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis rightAxis = barChartStats.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = barChartStats.getLegend();
        legend.setEnabled(false);
    }

    private void updateBarChart(int trainings, int wins, int losses, int damage) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, trainings));
        entries.add(new BarEntry(1f, wins));
        entries.add(new BarEntry(2f, losses));
        entries.add(new BarEntry(3f, damage));

        BarDataSet dataSet = new BarDataSet(entries, "Statistics");
        dataSet.setColors(
                Color.rgb(66, 133, 244),
                Color.rgb(52, 168, 83),
                Color.rgb(234, 67, 53),
                Color.rgb(251, 188, 5)
        );
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChartStats.setData(barData);
        barChartStats.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(new String[]{"Train", "Wins", "Losses", "Damage"})
        );
        barChartStats.setFitBars(true);
        barChartStats.invalidate();
    }

    private void setupPieChart() {
        Description description = new Description();
        description.setText("Crew by location");
        pieChartLocations.setDescription(description);
        pieChartLocations.setUsePercentValues(false);
        pieChartLocations.setDrawHoleEnabled(true);
        pieChartLocations.setCenterText("Crew");
        pieChartLocations.setEntryLabelTextSize(12f);
    }

    private void updatePieChart() {
        int quarters = storage.getByLocation(Location.QUARTERS).size();
        int simulator = storage.getByLocation(Location.SIMULATOR).size();
        int missionControl = storage.getByLocation(Location.MISSION_CONTROL).size();
        int medbay = storage.getByLocation(Location.MEDBAY).size();

        List<PieEntry> entries = new ArrayList<>();
        if (quarters > 0) entries.add(new PieEntry(quarters, "Quarters"));
        if (simulator > 0) entries.add(new PieEntry(simulator, "Simulator"));
        if (missionControl > 0) entries.add(new PieEntry(missionControl, "Mission"));
        if (medbay > 0) entries.add(new PieEntry(medbay, "Medbay"));

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "No Crew"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Locations");
        dataSet.setColors(
                Color.rgb(66, 133, 244),
                Color.rgb(52, 168, 83),
                Color.rgb(251, 188, 5),
                Color.rgb(234, 67, 53)
        );
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChartLocations.setData(pieData);
        pieChartLocations.invalidate();
    }
}