package me.askfix.api.askfix.events;

public class DisconnectEvent {
    private final String data;

    public DisconnectEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
