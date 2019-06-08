package it.polimi.ingsw.model.cards;

public class Teleporter extends Powerup {
    @Override
    public boolean isUsableDuringTurn() {
        return true;
    }

    @Override
    public String toString() {
        return "Teleporter - " + this.color.toString();
    }
}
