package com.lssl.spacecolony.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crewMember);
    }

    private List<CrewMember> crewList = new ArrayList<>();
    private OnCrewClickListener onCrewClickListener;

    public void setCrewList(List<CrewMember> crewList) {
        this.crewList = crewList;
        notifyDataSetChanged();
    }

    public void setOnCrewClickListener(OnCrewClickListener listener) {
        this.onCrewClickListener = listener;
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

        holder.itemView.setOnClickListener(v -> {
            if (onCrewClickListener != null) {
                onCrewClickListener.onCrewClick(crewMember);
            }
        });
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
    private int getSpecColor(CrewViewHolder holder, Specialization specialization) {
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

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCrewAvatar;
        TextView tvCrewName;
        TextView tvCrewSpec;
        TextView tvCrewStats;
        TextView tvCrewLocation;
        ProgressBar progressEnergy;

        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCrewAvatar = itemView.findViewById(R.id.ivCrewAvatar);
            tvCrewName = itemView.findViewById(R.id.tvCrewName);
            tvCrewSpec = itemView.findViewById(R.id.tvCrewSpec);
            tvCrewStats = itemView.findViewById(R.id.tvCrewStats);
            tvCrewLocation = itemView.findViewById(R.id.tvCrewLocation);
            progressEnergy = itemView.findViewById(R.id.progressEnergy);
        }
    }
}