package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class SimpleWeapon extends Weapon {

    /**
     * Override of the useEffect method when the Weapon is a simple one
     * @param shooter it's the player who is using the effect
     * @param effect it's the effect that is being used
     * @param forPotentiableWeapons it's an ArrayList containing all of the damages made by other effect (if the weapon is potentiable)
     * @param map it's the GameMap
     * @param players it's an ArrayList containing all of the Players in game
     * @return an ArrayList containing all of the possible solutions of damages
     */
    @Override
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, ArrayList<Damage> forPotentiableWeapons, GameMap map, ArrayList<Player> players) {
        return computeDamages(effect, shooter, null, map, players);
    }

}
