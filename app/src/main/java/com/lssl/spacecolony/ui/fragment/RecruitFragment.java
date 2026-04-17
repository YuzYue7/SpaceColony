package com.lssl.spacecolony.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.data.AppRepository;
import com.lssl.spacecolony.data.Storage;
import com.lssl.spacecolony.manager.CrewFactory;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Specialization;

public class RecruitFragment extends Fragment {

    private Storage storage;

    public RecruitFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recruit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = AppRepository.getInstance().getStorage();

        EditText etCrewName = view.findViewById(R.id.etCrewName);
        Spinner spinnerSpecialization = view.findViewById(R.id.spinnerSpecialization);
        Button btnCreateCrew = view.findViewById(R.id.btnCreateCrew);
        Button btnBackHome = view.findViewById(R.id.btnBackHomeFromRecruit);

        ArrayAdapter<Specialization> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Specialization.values()
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialization.setAdapter(spinnerAdapter);

        btnCreateCrew.setOnClickListener(v -> {
            String name = etCrewName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (storage.containsCrewName(name)) {
                Toast.makeText(requireContext(), "A crew member with this name already exists.", Toast.LENGTH_SHORT).show();
                return;
            }

            Specialization specialization = (Specialization) spinnerSpecialization.getSelectedItem();
            CrewMember crewMember = CrewFactory.createCrewMember(name, specialization);
            storage.add(crewMember);

            Toast.makeText(requireContext(),
                    "Crew member created: " + crewMember.getName(),
                    Toast.LENGTH_SHORT).show();

            etCrewName.setText("");
            spinnerSpecialization.setSelection(0);
        });

        btnBackHome.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit()
        );
    }
}