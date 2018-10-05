package me.askfix.api.askfix.dialogs;

import android.content.Context;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import me.askfix.api.askfix.R;

public class DataDialog {
    private TextView tvChannelName;
    private TextView tvData;
    private
    MaterialDialog dialog;

    public DataDialog(Context context) {
        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_data, false)
                .canceledOnTouchOutside(true)
                .cancelable(true)
                .build();

        tvChannelName = (TextView) dialog.findViewById(R.id.tvChannelName);
        tvData = (TextView) dialog.findViewById(R.id.tvData);


    }

    public void setData(String channelName, String data){
        tvChannelName.setText(channelName);
        tvData.setText(data);
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
