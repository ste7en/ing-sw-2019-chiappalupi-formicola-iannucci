package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class Powerup {

    /**
     * Type of the powerup
     */
    private PowerupType type;

    /**
     * Description of the powerup
     */
    private String description;

    /**
     * Color of the powerup
     */
    private AmmoColor color;

    /**
     * Powerup's type getter
     * @return the type of the powerup
     */
    public PowerupType getType() {
        return type;
    }

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

    /*All of the methods below need the view to be implemented, so they will be partially written and not tested*/

    /**
     * Method that handles the effect of the powerup
     * @param user the player who is using the powerup
     * @param target the player who is the target of the powerup (null if PowerupType is Teleporter)
     * @param gameMap necessary to manage the effects (null if PowerupType is TargetingScope)
     * @throws NullPointerException if user is null
     * @throws RuntimeException if user and target are the same player
     */
    public void use(Player user, Player target, GameMap gameMap) {
        if(user == null) throw new NullPointerException("user can't be null");
        if(user == target) throw new RuntimeException("user and target can't be the same player");
        switch (type) {
            case Newton: {
                newton(user, target, gameMap);
                return;
            }
            case TagbackGrenade: {
                tagbackGrenade(user.getCharacter().getColor(), target.getPlayerBoard(), gameMap);
                return;
            }
            case TargetingScope: {
                targetingScope(user.getCharacter().getColor(), target.getPlayerBoard());
                return;
            }
            case Teleporter: {
                teleporter(user, gameMap);
                return;
            }
        }
    }

    /**
     * Private method that handles the effect of the Targeting Scope powerup
     * @param shooter the one who shoots
     * @param shot the one who gets shot
     */
    private void targetingScope(PlayerColor shooter, PlayerBoard shot) {
        shot.appendDamage(shooter, 1);
    }

    /**
     * Private method that handles the effect of the Newton powerup
     * @param user player to be moved
     * @param gameMap necessary to move the player
     */
    private void newton(Player user, Player target, GameMap gameMap) {
        //toDo
    }

    /**
     * Private method that handles the effect of the TagbackGrenade powerup
     * @param shooter the one who give the mark
     * @param shot the one who gets the mark
     * @param gameMap necessary to verify if the shooter can see the shot
     */
    private void tagbackGrenade(PlayerColor shooter, PlayerBoard shot, GameMap gameMap) {
        //if(gameMap.canSee(shooter, shot)
        ArrayList<PlayerColor>marks = new ArrayList<>();
        marks.add(shooter);
        shot.setMarks(marks);
    }

    /**
     * Private method that handles the effect of the Teleporter powerup
     * @param player the player that is using the Teleporter
     * @param gameMap necessary to teleport
     */
    private void teleporter(Player player, GameMap gameMap) {
        //toDo
    }

}