package dev.menace.event.events;

public class EventMouse extends Event {

    int button;

    public EventMouse(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

}
