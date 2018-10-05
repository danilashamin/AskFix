package me.askfix.api.askfix.events;

public class DataReceivedEvent {
    private final String data;

    public DataReceivedEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
