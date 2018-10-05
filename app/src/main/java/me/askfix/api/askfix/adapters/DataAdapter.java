package me.askfix.api.askfix.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.askfix.api.askfix.R;
import me.askfix.api.askfix.utils.ChannelNameExtractor;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private final Context context;
    private final List<String> channels;
    private final OnDataClickListener listener;
    private final List<String> dataSet;

    public DataAdapter(Context context, List<String> channels, OnDataClickListener listener) {
        this.context = context;
        this.channels = channels;
        this.listener = listener;
        dataSet = new ArrayList<>();
        dataSet.addAll(channels);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_data, viewGroup, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {
        viewHolder.tvChannelName.setText(channels.get(i));
        viewHolder.tvData.setText(dataSet.get(i));
        viewHolder.tvData.setOnClickListener(view -> listener.onDataClick(channels.get(i), dataSet.get(i)));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public void updateData(String data) {
        String channelName = ChannelNameExtractor.getChannelName(data);
        for (int i = 0; i < channels.size() - 1; i++) {
            String currentChannelName = channels.get(i);
            if (channelName.equals(currentChannelName)) {
                dataSet.set(i, data);
                notifyItemChanged(i);
            }
        }
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView tvChannelName;
        TextView tvData;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChannelName = itemView.findViewById(R.id.tvChannelName);
            tvData = itemView.findViewById(R.id.tvData);
        }
    }
}
