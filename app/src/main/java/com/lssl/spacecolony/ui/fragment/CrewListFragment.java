package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.lssl.spacecolony.ui.adapter.CrewAdapter;

public class CrewListFragment extends Fragment {

    private CrewAdapter adapter;
    private Storage storage;
    private EditText etRenameCrew;

    public CrewListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crew_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCrew);
        Button btnRenameCrew = view.findViewById(R.id.btnRenameCrew);
        Button btnBack = view.findViewById(R.id.btnBackHomeFromList);
        etRenameCrew = view.findViewById(R.id.etRenameCrew);

        adapter = new CrewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnCrewClickListener(crewMember ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, CrewDetailFragment.newInstance(crewMember.getId()))
                        .commit()
        );

        refreshList();

        btnRenameCrew.setOnClickListener(v -> renameByTypedName());

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

    private void renameByTypedName() {
        String input = etRenameCrew.getText().toString().trim();

        if (TextUtils.isEmpty(input)) {
            Toast.makeText(requireContext(), "Enter: oldName,newName", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = input.split(",");
        if (parts.length != 2) {
            Toast.makeText(requireContext(), "Use format: oldName,newName", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldName = parts[0].trim();
        String newName = parts[1].trim();

        if (TextUtils.isEmpty(oldName) || TextUtils.isEmpty(newName)) {
            Toast.makeText(requireContext(), "Both old and new names are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        CrewMember target = null;
        for (CrewMember crewMember : storage.getAll()) {
            if (crewMember.getName().equalsIgnoreCase(oldName)) {
                target = crewMember;
                break;
            }
        }

        if (target == null) {
            Toast.makeText(requireContext(), "Crew member not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (storage.containsCrewNameExceptId(newName, target.getId())) {
            Toast.makeText(requireContext(), "Another crew member already has this name.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = storage.renameCrewMember(target.getId(), newName);
        if (success) {
            Toast.makeText(requireContext(), "Crew member renamed successfully.", Toast.LENGTH_SHORT).show();
            etRenameCrew.setText("");
            refreshList();
        } else {
            Toast.makeText(requireContext(), "Rename failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshList() {
        if (adapter != null && storage != null) {
            adapter.setCrewList(storage.getAll());
        }
    }
}