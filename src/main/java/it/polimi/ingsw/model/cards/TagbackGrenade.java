package it.polimi.ingsw.model.cards;

public class TagbackGrenade extends Powerup {
    @Override
    public boolean isUsableDuringTurn() {
        return false;
    }

    @Override
    public String toString() {
        return "TagbackGrenade - " + this.color.toString();
    }
}
