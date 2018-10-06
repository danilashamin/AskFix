package me.askfix.api.askfix.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import me.askfix.api.askfix.R;
import me.askfix.api.askfix.utils.ChannelNameExtractor;
import me.askfix.api.askfix.utils.MessageSetUtils;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private final Context context;
    private final List<String> channels;
    private final OnDataClickListener listener;
    private final List<List<String>> dataSet;

    public DataAdapter(Context context, List<String> channels, OnDataClickListener listener) {
        this.context = context;
        this.channels = channels;
        this.listener = listener;
        dataSet = new ArrayList<>(channels.size());
        initDataSet();
    }

    private void initDataSet() {
        for (String ignored : channels) {
            dataSet.add(new ArrayList<>());
        }
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_data, viewGroup, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {
        if (dataSet.size() == 0) {
            return;
        }
        List<String> messages = dataSet.get(i);
        MessageSetUtils.addMessagesToContainer(viewHolder.llContainer, messages, channels.get(i), MessageSetUtils.Ellipsize.ELLIPSIZE);
        viewHolder.llContainer.setOnClickListener(view -> listener.onDataClick(channels.get(i), dataSet.get(i)));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public void updateData(String data) {
        String channelName = ChannelNameExtractor.getChannelName(data);
        for (int i = 0; i < channels.size(); i++) {
            String currentChannelName = channels.get(i);
            if (channelName.equals(currentChannelName)) {
                dataSet.get(i).add(data);
                notifyItemChanged(i);
            }
        }
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llContainer;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            llContainer = itemView.findViewById(R.id.llContainer);
        }
    }
}
