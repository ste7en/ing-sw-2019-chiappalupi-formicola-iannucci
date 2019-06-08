package it.polimi.ingsw.model.cards;

public class TargetingScope extends Powerup {
    @Override
    public boolean isUsableDuringTurn() {
        return false;
    }

    @Override
    public String toString() {
        return "TargetingScope - " + this.color.toString();
    }
}
