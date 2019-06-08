package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Newton extends Powerup {

    private static int distance = 2;

    @Override
    public boolean isUsableDuringTurn() {
        return true;
    }

    @Override
    public List<Damage> use(Player shooter, Board board, List<Player> players) {
        List<Cell> possibleMovements = new ArrayList<>();
        List<Damage> possibleDamages = new ArrayList<>();
        for(Player target : players)
            if(target != shooter) {
                Weapon.getMovementsFromDirections(target, (GameMap)board.getMap().clone(), possibleMovements, distance);
                Set<Cell> set = new HashSet<>(possibleMovements);
                possibleMovements.clear();
                possibleMovements.addAll(set);
                possibleMovements.remove(board.getMap().getCellFromPlayer(target));
                for(Cell movement : possibleMovements) {
                    Damage damage = new Damage();
                    damage.setTarget(target);
                    damage.setPosition(movement);
                    possibleDamages.add(damage);
                }
                possibleMovements.clear();
            }
        return possibleDamages;
    }

    @Override
    public String toString() {
        return "Newton - " + this.color.toString();
    }
}
