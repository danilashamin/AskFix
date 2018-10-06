package me.askfix.api.askfix.dialogs;

import android.content.Context;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import me.askfix.api.askfix.R;
import me.askfix.api.askfix.utils.MessageSetUtils;

public class DataDialog {
    private LinearLayout llDialogContainer;
    private MaterialDialog dialog;

    public DataDialog(Context context) {
        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.layout_data, true)
                .canceledOnTouchOutside(true)
                .cancelable(true)
                .build();

        llDialogContainer = (LinearLayout) dialog.findViewById(R.id.llContainer);

    }

    public void setData(String channelName, List<String> data) {
        MessageSetUtils.addMessagesToContainer(llDialogContainer, data, channelName, MessageSetUtils.Ellipsize.NONE);
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

}
