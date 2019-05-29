package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.utility.Direction;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.networking.utility.CommunicationMessage;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class SelectableWeapon extends Weapon {
    /**
     * Override of the useEffect method when the Weapon is a selectable one
     * @param shooter it's the player who is using the effect
     * @param effect it's the effect that is being used
     * @param forPotentiableWeapons it's an ArrayList containing all of the damages made by other effect (if the weapon is potentiable)
     * @param map it's the GameMap
     * @param players it's an ArrayList containing all of the Players in game
     * @return an ArrayList containing all of the possible solutions of damages
     */
    @Override
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, ArrayList<Damage> forPotentiableWeapons, GameMap map, ArrayList<Player> players) {
        if (effect.getProperties().containsKey(EffectProperty.CanMoveBefore))
            return computeDamageCanMoveBefore(effect, shooter, null, map, players);
        if(effect.getProperties().containsKey(EffectProperty.Hard))
            if(effect.getProperties().get(EffectProperty.Hard) == 0) return inBarbecueMode(shooter, map);
        return computeDamages(effect, shooter, null, map, players);
    }

    /**
     * Used to ask the message to return when using the weapon of the weapon.
     *
     * @return the message type that needs to be sent.
     */
    @Override
    public CommunicationMessage type() {
        return CommunicationMessage.MODES_LIST;
    }

    /**
     * This method computes the different solutions of damages when using the "in barbecue mode" effect, of the "Flamethrower" weapon
     * @param shooter it's the Player who is using the effect
     * @param map it's the GameMap
     * @return an ArrayList containing all of the different solutions
     */
    private ArrayList<ArrayList<Damage>> inBarbecueMode(Player shooter, GameMap map) {
        ArrayList<ArrayList<Damage>> damages = new ArrayList<>();
        for(Direction direction : Direction.values())
            if(map.getTargetsFromDirection(shooter, direction).size() > 0) {
                ArrayList<Damage> arrayD = new ArrayList<>();
                damages.add(arrayD);
                ArrayList<Player> targetsInDirection = map.getTargetsFromDirection(shooter, direction);
                targetsInDirection.removeAll(map.getTargetsInMyCell(shooter));
                for(Player player : targetsInDirection) {
                    Damage damage = new Damage();
                    damage.setTarget(player);
                    if(map.getTargetsAtMaxDistance(shooter, 1).contains(player)) {
                        damage.setDamage(2);
                        arrayD.add(damage);
                    } else if(map.getTargetsAtMaxDistance(shooter, 2).contains(player)) {
                        damage.setDamage(1);
                        arrayD.add(damage);
                    }
                }
                if(arrayD.size() == 0) damages.remove(arrayD);
            }
        return damages;
    }
}
