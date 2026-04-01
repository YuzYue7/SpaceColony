package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.data.AppRepository;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.ui.adapter.SelectableCrewAdapter;

import java.util.List;

public class QuartersFragment extends Fragment {

    private Storage storage;
    private SelectableCrewAdapter adapter;

    public QuartersFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quarters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerQuarters);
        Button btnMoveToSimulator = view.findViewById(R.id.btnMoveToSimulator);
        Button btnMoveToMissionControl = view.findViewById(R.id.btnMoveToMissionControl);
        Button btnBack = view.findViewById(R.id.btnBackHomeFromQuarters);

        adapter = new SelectableCrewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        refreshList();

        btnMoveToSimulator.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelectedCrew();
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one crew member.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CrewMember crewMember : selected) {
                storage.moveCrewMember(crewMember.getId(), Location.SIMULATOR);
            }

            Toast.makeText(requireContext(), "Moved to Simulator.", Toast.LENGTH_SHORT).show();
            refreshList();
        });

        btnMoveToMissionControl.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelectedCrew();
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one crew member.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CrewMember crewMember : selected) {
                storage.moveCrewMember(crewMember.getId(), Location.MISSION_CONTROL);
            }

            Toast.makeText(requireContext(), "Moved to Mission Control.", Toast.LENGTH_SHORT).show();
            refreshList();
        });

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit()
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        if (adapter != null && storage != null) {
            adapter.setCrewList(storage.getByLocation(Location.QUARTERS));
        }
    }
}