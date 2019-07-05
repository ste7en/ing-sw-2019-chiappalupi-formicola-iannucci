package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Teleporter extends Powerup {

    @Override
    public boolean isUsableDuringTurn() {
        return true;
    }

    @Override
    public boolean isUsableAfterDamageMade() {
        return false;
    }

    @Override
    public boolean isUsableAfterDamageTaken() {
        return false;
    }

    @Override
    public List<Damage> use(Player player, GameMap map, List<Player> players) {
        List<Damage> possibleDamages = new ArrayList<>();
        int rows = map.getRows();
        int columns = map.getColumns();
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < columns; j++) {
                Damage d = new Damage();
                d.setTarget(player);
                d.setPosition(map.getCell(i, j));
                possibleDamages.add(d);
            }
        possibleDamages.sort(Damage::compareTo);
        return possibleDamages;
    }

    @Override
    public String toString() {
        return "Teleporter - " + this.color.toString();
    }
}
