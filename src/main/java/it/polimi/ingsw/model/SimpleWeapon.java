package it.polimi.ingsw.model;

import java.util.ArrayList;

public class SimpleWeapon extends Weapon {

    @Override
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, Weapon weapon, ArrayList<Damage> forPotentiableWeapons, GameMap map, ArrayList<Player> players) {
        return computeDamages(effect, shooter, null, map, players);
    }

}
