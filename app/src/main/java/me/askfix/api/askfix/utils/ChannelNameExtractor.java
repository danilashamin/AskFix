package me.askfix.api.askfix.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChannelNameExtractor {
    private static final String MESSAGE_FIELD = "message";
    private static final String HEADER_FIELD = "header";
    private static final String TO_FIELD = "to";
    private static final String NAME_FIELD = "name";


    public static String getChannelName(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getJSONObject(MESSAGE_FIELD).getJSONObject(HEADER_FIELD).getJSONObject(TO_FIELD).getString(NAME_FIELD);
        } catch (JSONException e) {
            return "";
        }
    }
}
