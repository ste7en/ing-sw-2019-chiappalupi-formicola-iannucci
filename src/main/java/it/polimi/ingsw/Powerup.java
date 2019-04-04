package it.polimi.ingsw;

public class Powerup {

    private final String name;
    private final String description;
    private final AmmoColor color;

    public Powerup(String name, String description, AmmoColor color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public AmmoColor getColor() {
        return color;
    }

}