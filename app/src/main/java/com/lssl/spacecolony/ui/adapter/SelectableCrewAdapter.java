package com.lssl.spacecolony.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Location;
import com.lssl.spacecolony.model.enums.Specialization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableCrewAdapter extends RecyclerView.Adapter<SelectableCrewAdapter.SelectableCrewViewHolder> {

    private List<CrewMember> crewList = new ArrayList<>();
    private final Set<Integer> selectedIds = new HashSet<>();

    public void setCrewList(List<CrewMember> crewList) {
        this.crewList = crewList;
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<CrewMember> getSelectedCrew() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember crewMember : crewList) {
            if (selectedIds.contains(crewMember.getId())) {
                selected.add(crewMember);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public SelectableCrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selectable_crew, parent, false);
        return new SelectableCrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableCrewViewHolder holder, int position) {
        CrewMember crewMember = crewList.get(position);

        holder.tvCrewName.setText(crewMember.getName());
        holder.tvCrewSpec.setText(crewMember.getSpecialization().name());
        holder.tvCrewSpec.setTextColor(getSpecColor(holder, crewMember.getSpecialization()));
        holder.tvCrewStats.setText(
                "Skill " + crewMember.getBaseSkill()
                        + "  •  Exp " + crewMember.getExperience()
                        + "  •  Res " + crewMember.getResilience()
                        + "  •  Energy " + crewMember.getEnergy() + "/" + crewMember.getMaxEnergy()
        );
        holder.tvCrewLocation.setText(getLocationLabel(crewMember.getLocation()));
        holder.progressEnergy.setMax(Math.max(1, crewMember.getMaxEnergy()));
        holder.progressEnergy.setProgress(Math.max(0, crewMember.getEnergy()));

        bindAvatar(holder.ivCrewAvatar, crewMember.getSpecialization());

        holder.checkCrew.setOnCheckedChangeListener(null);
        holder.checkCrew.setChecked(selectedIds.contains(crewMember.getId()));

        holder.checkCrew.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIds.add(crewMember.getId());
            } else {
                selectedIds.remove(crewMember.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> holder.checkCrew.setChecked(!holder.checkCrew.isChecked()));
    }

    private String getLocationLabel(Location location) {
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

    @ColorInt
    private int getSpecColor(SelectableCrewViewHolder holder, Specialization specialization) {
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
        return ContextCompat.getColor(holder.itemView.getContext(), colorRes);
    }

    private void bindAvatar(ImageView avatarView, Specialization specialization) {
        switch (specialization) {
            case PILOT:
                avatarView.setImageResource(R.drawable.ic_pilot);
                break;
            case ENGINEER:
                avatarView.setImageResource(R.drawable.ic_engineer);
                break;
            case MEDIC:
                avatarView.setImageResource(R.drawable.ic_medic);
                break;
            case SCIENTIST:
                avatarView.setImageResource(R.drawable.ic_scientist);
                break;
            case SOLDIER:
                avatarView.setImageResource(R.drawable.ic_soldier);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return crewList == null ? 0 : crewList.size();
    }

    static class SelectableCrewViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkCrew;
        ImageView ivCrewAvatar;
        TextView tvCrewName;
        TextView tvCrewSpec;
        TextView tvCrewStats;
        TextView tvCrewLocation;
        ProgressBar progressEnergy;

        public SelectableCrewViewHolder(@NonNull View itemView) {
            super(itemView);
            checkCrew = itemView.findViewById(R.id.checkCrew);
            ivCrewAvatar = itemView.findViewById(R.id.ivCrewAvatar);
            tvCrewName = itemView.findViewById(R.id.tvCrewName);
            tvCrewSpec = itemView.findViewById(R.id.tvCrewSpec);
            tvCrewStats = itemView.findViewById(R.id.tvCrewStats);
            tvCrewLocation = itemView.findViewById(R.id.tvCrewLocation);
            progressEnergy = itemView.findViewById(R.id.progressEnergy);
        }
    }
}