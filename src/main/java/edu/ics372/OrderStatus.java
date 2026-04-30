package edu.ics372;

// Enum representing valid order states

public enum OrderStatus {
    NEW("new"),
    INCOMING("incoming"),
    STARTED("started"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String cssClass;

    OrderStatus(String cssClass) {
        this.cssClass = cssClass;
    }

    public String css() {
        return cssClass;
    }
}