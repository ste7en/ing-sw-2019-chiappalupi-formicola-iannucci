package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PotentiableWeapon extends Weapon {
    @Override
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, Weapon weapon, ArrayList<Damage> forPotentiableWeapons, GameMap map, ArrayList<Player> players) {
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        if(weapon.getEffects().get(0) == effect) {
            boolean canMoveBefore = effect.getProperties().containsKey(EffectProperty.CanMoveBefore);
            return canMoveBefore ? computeDamageCanMoveBefore(effect, shooter, null, map, players) : computeDamages(effect, shooter, null, map, players);
        }
        else {
            if(effect.getProperties().containsKey(EffectProperty.MoveMe) && effect.getProperties().get(EffectProperty.MoveMe) > 0) {
                ArrayList<Cell> movements = new ArrayList<>();
                recursiveMovementsEverywhere(movements, effect.getProperties().get(EffectProperty.MoveMe), shooter, map);
                Set<Cell> movementsDuplicateEliminator = new HashSet<>(movements);
                movements.clear();
                movements.addAll(movementsDuplicateEliminator);
                movements.sort(Cell::compareTo);
                for(Cell cell : movements)
                    if(cell != map.getCellFromPlayer(shooter)) {
                        Damage damage = new Damage();
                        damage.setTarget(shooter);
                        damage.setPosition(cell);
                        ArrayList<Damage> arrayD = new ArrayList<>();
                        arrayD.add(damage);
                        solutions.add(arrayD);
                    }
                return solutions;
            }
            if(effect.getProperties().containsKey(EffectProperty.AdditionalTarget)) {
                if(forPotentiableWeapons == null) throw new NullPointerException("This effects needs the history of the damages made by the other effects to work!");
                if(forPotentiableWeapons.size() == 0) throw new RuntimeException("The history of damages made by the other effects can't be empty!");
                Set<Player> setForAdditionalEffects = new HashSet<>();
                for(Damage damage : forPotentiableWeapons) setForAdditionalEffects.add(damage.getTarget());
                ArrayList<Player> listForAdditionalEffects = new ArrayList<>(setForAdditionalEffects);
                return computeDamages(effect, shooter, listForAdditionalEffects, map, players);
            }
            if(effect.getProperties().containsKey(EffectProperty.EffectOnTarget)) {
                if(forPotentiableWeapons == null) throw new NullPointerException("This effects needs the history of the damages made by the other effects to work!");
                if(forPotentiableWeapons.size() == 0) throw new RuntimeException("The history of damages made by the other effects can't be empty!");
                int effectOnTarget = effect.getProperties().get(EffectProperty.EffectOnTarget);
                if(effectOnTarget < 0) effectOnTarget = (-1) * effectOnTarget;
                if(forPotentiableWeapons.size() == 0) return new ArrayList<>();
                ArrayList<Player> toNotShoot = new ArrayList<>();
                toNotShoot.add(shooter);
                if(! effect.getProperties().containsKey(EffectProperty.MultipleCell)) for(Damage damage : forPotentiableWeapons) toNotShoot.add(damage.getTarget());
                if(effect.getProperties().get(EffectProperty.EffectOnTarget) >= 0) solutions = useEffect(forPotentiableWeapons.get(effectOnTarget).getTarget(), weapon.getEffects().get(0), weapon, null, map, players);
                else {
                    Effect box = new Effect();
                    HashMap<EffectProperty, Integer> boxProperties = (HashMap<EffectProperty, Integer>)weapon.getEffects().get(effectOnTarget).getProperties().clone();
                    boxProperties.remove(EffectProperty.EffectOnTarget);
                    box.setProperties(boxProperties);
                    solutions = computeDamages(box, forPotentiableWeapons.get(0).getTarget(), null, map, players);
                    if (boxProperties.containsKey(EffectProperty.MultipleCell) && boxProperties.get(EffectProperty.MultipleCell) == 0) {
                        Damage damage = new Damage();
                        damage.setTarget(forPotentiableWeapons.get(0).getTarget());
                        if(effect.getProperties().containsKey(EffectProperty.Damage)) damage.setDamage(effect.getProperties().get(EffectProperty.Damage));
                        if(effect.getProperties().containsKey(EffectProperty.Mark)) damage.setDamage(effect.getProperties().get(EffectProperty.Mark));
                        int i = 0;
                        for(Damage d : solutions.get(0))
                            if(d.getTarget().compareTo(damage.getTarget()) < 0) i++;
                        solutions.get(0).add(i, damage);
                    }

                }
                ArrayList<ArrayList<Damage>> toRemove = new ArrayList<>();
                boolean condition = !effect.getProperties().get(EffectProperty.Damage).equals(weapon.getEffects().get(0).getProperties().get(EffectProperty.Damage));
                int updatedDamage = effect.getProperties().get(EffectProperty.Damage);
                if(condition) updatedDamage = effect.getProperties().get(EffectProperty.Damage);
                for(ArrayList<Damage> damages : solutions)
                    for(Damage damage : damages) {
                        if(toNotShoot.contains(damage.getTarget()))
                            toRemove.add(damages);
                        if(condition) damage.setDamage(updatedDamage);
                    }
                solutions.removeAll(toRemove);
                return solutions;
            }
            if(effect.getProperties().containsKey(EffectProperty.CanMoveBefore)) return computeDamageCanMoveBefore(effect, shooter, forPotentiableWeapons, map, players);
            if(effect.getProperties().containsKey(EffectProperty.Hard))
                if(effect.getProperties().get(EffectProperty.Hard) == 2) {
                    if(forPotentiableWeapons == null) throw new NullPointerException("This effects needs the history of the damages made by the other effects to work!");
                    if(forPotentiableWeapons.size() == 0) throw new RuntimeException("The history of damages made by the other effects can't be empty!");
                    return withTurretTripod(shooter, forPotentiableWeapons, players, map);
                }
            return computeDamages(effect, shooter, null, map, players);
        }
    }

    /**
     * This method computes the different solutions of damages when using the "with turret tripod" effect, of the "Machine Gun" weapon
     * @param shooter it's the player who is using the effect
     * @param earlyDamages it's the result of the other effects of the weapon
     * @param players it's an ArrayList containing all of the players in the game
     * @param map it's the GameMap
     * @return an ArrayList containing all of the different solutions
     */
    private ArrayList<ArrayList<Damage>> withTurretTripod(Player shooter, ArrayList<Damage> earlyDamages, ArrayList<Player> players, GameMap map) {
        ArrayList<ArrayList<Damage>> solutions;
        Player alreadyShot = null;
        ArrayList<Player> toShoot = new ArrayList<>();
        for(Player player : players) {
            int counter = 0;
            for(Damage damage : earlyDamages)
                if(damage.getTarget() == player) counter++;
            if (counter == 2) alreadyShot = player;
            else if(counter == 1) toShoot.add(player);
        }
        Effect boxEffect = new Effect();
        HashMap<EffectProperty, Integer> boxProperties = new HashMap<>();
        boxProperties.put(EffectProperty.MaxPlayer, 2);
        boxProperties.put(EffectProperty.MaxDistance, -1);
        boxProperties.put(EffectProperty.Damage, 1);
        boxEffect.setProperties(boxProperties);
        solutions = computeDamages(boxEffect, shooter, null, map, players);
        Set<ArrayList<Damage>> toRemove = new HashSet<>();
        if(alreadyShot != null)
            for(ArrayList<Damage> damages : solutions)
                for(Damage damage : damages)
                    if(damage.getTarget() == alreadyShot) toRemove.add(damages);
        if(toShoot.size() > 0)
            for (ArrayList<Damage> damages : solutions)
                if (damages.size() > 1) {
                    if(toShoot.size() == 1) {
                        int counter = 0;
                        for (Damage damage : damages)
                            if (damage.getTarget() != toShoot.get(0)) counter++;
                        if (counter != 1) toRemove.add(damages);
                    } else {
                        ArrayList<Player> boxTargets = new ArrayList<>();
                        for(Damage damage : damages) boxTargets.add(damage.getTarget());
                        if(boxTargets.contains(toShoot.get(0)) && boxTargets.contains(toShoot.get(1))) toRemove.add(damages);
                    }
                }
        solutions.removeAll(toRemove);
        return solutions;
    }

}
