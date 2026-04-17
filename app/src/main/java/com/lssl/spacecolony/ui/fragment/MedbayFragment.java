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

public class MedbayFragment extends Fragment {

    private Storage storage;
    private SelectableCrewAdapter adapter;

    public MedbayFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medbay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerMedbay);
        Button btnRecoverToQuarters = view.findViewById(R.id.btnRecoverToQuarters);
        Button btnBack = view.findViewById(R.id.btnBackHomeFromMedbay);

        adapter = new SelectableCrewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        refreshList();

        btnRecoverToQuarters.setOnClickListener(v -> {
            List<CrewMember> selected = adapter.getSelectedCrew();

            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one crew member.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CrewMember crewMember : selected) {
                storage.recoverCrewFromMedbay(crewMember.getId());
            }

            Toast.makeText(requireContext(),
                    "Crew recovered to Quarters.",
                    Toast.LENGTH_SHORT).show();

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
            adapter.setCrewList(storage.getByLocation(Location.MEDBAY));
        }
    }
}