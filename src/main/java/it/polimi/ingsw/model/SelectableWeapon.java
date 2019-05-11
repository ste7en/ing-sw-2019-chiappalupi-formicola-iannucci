package it.polimi.ingsw.model;

import java.util.ArrayList;

public class SelectableWeapon extends Weapon {
    @Override
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, Weapon weapon, ArrayList<Damage> forPotentiableWeapons, GameMap map, ArrayList<Player> players) {
        if (effect.getProperties().containsKey(EffectProperty.CanMoveBefore))
            return computeDamageCanMoveBefore(effect, shooter, null, map, players);
        if(effect.getProperties().containsKey(EffectProperty.Hard))
            if(effect.getProperties().get(EffectProperty.Hard) == 0) return inBarbecueMode(shooter, map);
        return computeDamages(effect, shooter, null, map, players);
    }

    /**
     * This method computes the different solutions of damages when using the "in barbecue mode" effect, of the "Flamethrower" weapon
     * @param shooter it's the Player who is using the effect
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
