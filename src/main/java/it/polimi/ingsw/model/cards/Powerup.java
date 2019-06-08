package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;

import java.util.List;

/**
 *
 * @author Daniele Chiappalupi
 */
public abstract class Powerup {

    /**
     * String constant used in messages between client-server
     */
    public static final String powerup_key = "POWERUP";

    /**
     * Description of the powerup
     */
    protected String description;

    /**
     * Color of the powerup
     */
    protected AmmoColor color;

    /**
     * Powerup's description getter
     * @return the description of the powerup
     */
    public String getDescription() {
        return description;
    }

    /**
     * Powerup's color getter
     * @return the color of the powerup
     */
    public AmmoColor getColor() {
        return color;
    }

    /**
     * Public method implemented by subclasses to know when a powerup can be used..
     * @return TRUE if the powerup can be used during the turn, FALSE if it must be used after other actions.
     */
    public abstract boolean isUsableDuringTurn();

    /**
     * Public method implemented by subclasses, used when a powerup is being used.
     * @param player it's the player who is using the powerup.
     * @param map it's the map of the game.
     * @param players it's the list of players in game.
     * @return the possible damages that a powerup can make.
     */
    public abstract List<Damage> use(Player player, GameMap map, List<Player> players);

}