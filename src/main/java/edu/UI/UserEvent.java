






package edu.UI;

import javafx.event.Event;
import javafx.event.EventType;

// Class designed to handle user events such as clicks, button presses, etc....
public class UserEvent extends Event {



    // from the documentation as an example.
    public static final EventType<UserEvent> ANY = new EventType<>(Event.ANY, "ANY");

    public UserEvent(EventType<? extends UserEvent> eventType) {
        super(eventType);
    }
}



