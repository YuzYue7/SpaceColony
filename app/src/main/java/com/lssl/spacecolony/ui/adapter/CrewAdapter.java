package com.lssl.spacecolony.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lssl.spacecolony.R;
import com.lssl.spacecolony.model.crew.CrewMember;
import com.lssl.spacecolony.model.enums.Specialization;

import java.util.ArrayList;
import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    private List<CrewMember> crewList = new ArrayList<>();

    public void setCrewList(List<CrewMember> crewList) {
        this.crewList = crewList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember crewMember = crewList.get(position);

        holder.tvCrewName.setText(crewMember.getName());
        holder.tvCrewSpec.setText("Specialization: " + crewMember.getSpecialization());
        holder.tvCrewStats.setText(
                "Skill: " + crewMember.getBaseSkill()
                        + " | Exp: " + crewMember.getExperience()
                        + " | Res: " + crewMember.getResilience()
                        + " | Energy: " + crewMember.getEnergy() + "/" + crewMember.getMaxEnergy()
        );
        holder.tvCrewLocation.setText("Location: " + crewMember.getLocation());

        bindAvatar(holder.ivCrewAvatar, crewMember.getSpecialization());
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

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCrewAvatar;
        TextView tvCrewName;
        TextView tvCrewSpec;
        TextView tvCrewStats;
        TextView tvCrewLocation;

        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCrewAvatar = itemView.findViewById(R.id.ivCrewAvatar);
            tvCrewName = itemView.findViewById(R.id.tvCrewName);
            tvCrewSpec = itemView.findViewById(R.id.tvCrewSpec);
            tvCrewStats = itemView.findViewById(R.id.tvCrewStats);
            tvCrewLocation = itemView.findViewById(R.id.tvCrewLocation);
        }
    }
}