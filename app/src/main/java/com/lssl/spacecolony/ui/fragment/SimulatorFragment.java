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

public class SimulatorFragment extends Fragment {

    private Storage storage;
    private SelectableCrewAdapter adapter;

    public SimulatorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simulator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerSimulator);
        Button btnTrainSelected = view.findViewById(R.id.btnTrainSelected);
        Button btnReturnToQuarters = view.findViewById(R.id.btnReturnToQuarters);
        Button btnBack = view.findViewById(R.id.btnBackHomeFromSimulator);

        adapter = new SelectableCrewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        refreshList();

        btnTrainSelected.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelectedCrew();
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one crew member.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CrewMember crewMember : selected) {
                crewMember.train();
            }

            Toast.makeText(requireContext(), "Training complete. Experience increased.", Toast.LENGTH_SHORT).show();
            refreshList();
        });

        btnReturnToQuarters.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelectedCrew();
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one crew member.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CrewMember crewMember : selected) {
                storage.moveCrewMember(crewMember.getId(), Location.QUARTERS);
            }

            Toast.makeText(requireContext(), "Returned to Quarters. Energy restored.", Toast.LENGTH_SHORT).show();
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
            adapter.setCrewList(storage.getByLocation(Location.SIMULATOR));
        }
    }
}