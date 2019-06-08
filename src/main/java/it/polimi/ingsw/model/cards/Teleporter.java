package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class Teleporter extends Powerup {
    @Override
    public boolean isUsableDuringTurn() {
        return true;
    }

    @Override
    public List<Damage> use(Player player, GameMap map, List<Player> players) {
        return null;
    }

    @Override
    public String toString() {
        return "Teleporter - " + this.color.toString();
    }
}
