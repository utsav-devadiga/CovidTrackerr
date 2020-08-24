package com.example.covidtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidtracker.R;
import com.example.covidtracker.api.timeseries.CasesTimeSeries;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {

    private ArrayList<CasesTimeSeries.Statewise> provListData = new ArrayList<>();
    private ListClickedListener listClickedListener;
    int confirmcase;
    int deathcase;
    int recoveredcase;
    int activecase;

    public StateAdapter(ArrayList<CasesTimeSeries.Statewise> provListData, ListClickedListener listClickedListener) {
        this.provListData = provListData;
        this.listClickedListener = listClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listClickedListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StateAdapter.ViewHolder holder, int position) {

            holder.mProvName.setText(provListData.get(position).getState());
            confirmcase = Integer.parseInt(provListData.get(position).getConfirmed());
            holder.mProvCase.setText(numberSeparator(confirmcase));
            deathcase = Integer.parseInt(provListData.get(position).getDeaths());
            holder.mProvDeath.setText(numberSeparator(deathcase));
            recoveredcase = Integer.parseInt(provListData.get(position).getRecovered());
            holder.mProvHealed.setText(numberSeparator(recoveredcase));
            activecase = Integer.parseInt(provListData.get(position).getActive());
            holder.mProvTreated.setText(numberSeparator(activecase));

        holder.mProvListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Position :::::::::::::::::::::::::::::::::: " + position);
                holder.mListClickedListener.onListClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return provListData.size();
    }

    public void filterList(ArrayList<CasesTimeSeries.Statewise> filteredList) {

        provListData = filteredList;
        notifyDataSetChanged();

    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mProvListLayout;

        TextView mProvName, mProvCase, mProvDeath, mProvHealed, mProvTreated;

        ListClickedListener mListClickedListener;

        public ViewHolder(@NonNull View itemView, ListClickedListener listClickedListener) {
            super(itemView);

            mProvListLayout = itemView.findViewById(R.id.prov_list_layout);

            mProvName = itemView.findViewById(R.id.prov_name);
            mProvCase = itemView.findViewById(R.id.prov_case);
            mProvDeath = itemView.findViewById(R.id.prov_death);
            mProvHealed = itemView.findViewById(R.id.prov_cured);
            mProvTreated = itemView.findViewById(R.id.prov_treated);

            mListClickedListener = listClickedListener;

        }
    }

    public interface ListClickedListener {
        void onListClicked(int position);
    }

    private String numberSeparator(int value) {
        return String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(value));
    }


}