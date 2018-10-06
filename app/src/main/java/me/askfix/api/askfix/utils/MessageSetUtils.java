package me.askfix.api.askfix.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class MessageSetUtils {
    private static TextView createTextView(Context context, Ellipsize ellipsize) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 0);
        textView.setLayoutParams(layoutParams);
        if(ellipsize == Ellipsize.ELLIPSIZE){
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(100);
            textView.setFilters(fArray);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        return textView;
    }

    public static void addMessagesToContainer(LinearLayout container, List<String> messages, String channelName, Ellipsize ellipsize) {
        container.removeAllViews();
        TextView tvChannelName = MessageSetUtils.createTextView(container.getContext(), ellipsize);
        tvChannelName.setTextSize(18);
        tvChannelName.setText(channelName);
        container.addView(tvChannelName);
        for (String message : messages) {
            TextView currentTextView = MessageSetUtils.createTextView(container.getContext(), ellipsize);
            currentTextView.setText(message);
            container.addView(currentTextView);
        }
    }

    public enum Ellipsize {ELLIPSIZE, NONE}
}
