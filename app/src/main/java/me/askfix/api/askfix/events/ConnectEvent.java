package me.askfix.api.askfix.events;

public class ConnectEvent {
    private final String data;

    public ConnectEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
