package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TagbackGrenade extends Powerup {
    @Override
    public boolean isUsableDuringTurn() {
        return false;
    }

    @Override
    public boolean isUsableAfterDamageMade() {
        return false;
    }

    @Override
    public boolean isUsableAfterDamageTaken() {
        return true;
    }

    @Override
    public List<Damage> use(Player player, GameMap map, List<Player> players) {
        List<Damage> damages = new ArrayList<>();
        for(Player target : players) {
            Damage damage = new Damage();
            damage.setTarget(target);
            damage.setMarks(1);
            damages.add(damage);
        }
        return damages;
    }

    @Override
    public String toString() {
        return "TagbackGrenade - " + this.color.toString();
    }
}
