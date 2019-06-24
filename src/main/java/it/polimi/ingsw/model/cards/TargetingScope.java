package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TargetingScope extends Powerup {

    @Override
    public boolean isUsableDuringTurn() {
        return false;
    }

    @Override
    public boolean isUsableAfterDamageMade() {
        return true;
    }

    @Override
    public boolean isUsableAfterDamageTaken() {
        return false;
    }

    @Override
    public List<Damage> use(Player player, GameMap map, List<Player> players) {
        List<Damage> possibleDamages = new ArrayList<>();
        for(Player target : players) {
            Damage d = new Damage();
            d.setTarget(target);
            d.setDamage(1);
            possibleDamages.add(d);
        }
        return possibleDamages;
    }

    @Override
    public String toString() {
        return "TargetingScope - " + this.color.toString();
    }
}
