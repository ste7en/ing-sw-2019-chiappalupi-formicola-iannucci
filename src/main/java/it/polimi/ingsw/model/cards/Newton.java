package it.polimi.ingsw.model.cards;

public class Newton extends Powerup {
    @Override
    public boolean isUsableDuringTurn() {
        return true;
    }

    @Override
    public String toString() {
        return "Newton - " + this.color.toString();
    }
}
