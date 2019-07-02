package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.networking.utility.CommunicationMessage;

import java.util.*;

/**
 *
 * @author Daniele Chiappalupi
 */
public class PotentiableWeapon extends Weapon {

    /**
     * String constant used in messages between client-server
     */
    public final static String forPotentiableWeapon_key = "FOR_POTENTIABLE_WEAPON";
    private static final String HISTORY_NEEDED = "This effects needs the history of the damages made by the other effects to work!";
    private static final String HISTORY_EMPTY = "The history of damages made by the other effects can't be empty!";

    /**
     * Override of the useEffect method when the Weapon is a potentiable one
     * @param shooter it's the player who is using the effect
     * @param effect it's the effect that is being used
     * @param forPotentiableWeapons it's an ArrayList containing all of the damages made by other effect (if the weapon is potentiable)
     * @param map it's the GameMap
     * @param players it's an ArrayList containing all of the Players in game
     * @return an ArrayList containing all of the possible solutions of damages
     */
    @Override
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, List<Damage> forPotentiableWeapons, GameMap map, List<Player> players) {
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        if(getEffects().get(0) == effect) {
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
                if(forPotentiableWeapons == null) throw new NullPointerException(HISTORY_NEEDED);
                if(forPotentiableWeapons.isEmpty()) throw new IllegalArgumentException(HISTORY_EMPTY);
                Set<Player> setForAdditionalEffects = new HashSet<>();
                for(Damage damage : forPotentiableWeapons) setForAdditionalEffects.add(damage.getTarget());
                ArrayList<Player> listForAdditionalEffects = new ArrayList<>(setForAdditionalEffects);
                return computeDamages(effect, shooter, listForAdditionalEffects, map, players);
            }
            if(effect.getProperties().containsKey(EffectProperty.EffectOnTarget)) {
                if(forPotentiableWeapons == null) throw new NullPointerException(HISTORY_NEEDED);
                if(forPotentiableWeapons.isEmpty()) throw new IllegalArgumentException(HISTORY_EMPTY);
                int effectOnTarget = effect.getProperties().get(EffectProperty.EffectOnTarget);
                if(effectOnTarget < 0) effectOnTarget = (-1) * effectOnTarget;
                if(forPotentiableWeapons.isEmpty()) return new ArrayList<>();
                ArrayList<Player> toNotShoot = new ArrayList<>();
                toNotShoot.add(shooter);
                if(! effect.getProperties().containsKey(EffectProperty.MultipleCell)) for(Damage damage : forPotentiableWeapons) toNotShoot.add(damage.getTarget());
                if(effect.getProperties().get(EffectProperty.EffectOnTarget) >= 0) solutions = useEffect(forPotentiableWeapons.get(effectOnTarget).getTarget(), getEffects().get(0), null, map, players);
                else {
                    Effect box = new Effect();
                    Map<EffectProperty, Integer> boxProperties = new EnumMap<>(getEffects().get(effectOnTarget).getProperties());
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
                boolean condition = !effect.getProperties().get(EffectProperty.Damage).equals(getEffects().get(0).getProperties().get(EffectProperty.Damage));
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
                    if(forPotentiableWeapons == null) throw new NullPointerException(HISTORY_NEEDED);
                    if(forPotentiableWeapons.size() == 0) throw new RuntimeException(HISTORY_EMPTY);
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
    private ArrayList<ArrayList<Damage>> withTurretTripod(Player shooter, List<Damage> earlyDamages, List<Player> players, GameMap map) {
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

    @Override
    public ArrayList<ArrayList<Integer>> effectsCombinations() {
        Map<Integer, ArrayList<Integer>> combinationsBox = combinationsWithLowerValues(effects.size(), effects.size());
        ArrayList<ArrayList<Integer>> combinations = new ArrayList<>();
        for(Integer i : combinationsBox.keySet())
            if (combinationsBox.get(i).contains(1))
                combinations.add(combinationsBox.get(i));
        boolean moveMe = false;
        boolean grenader = false;
        Integer effectPosition = -1;
        for(Effect effect : effects)
            if(effect.getProperties().containsKey(EffectProperty.MoveMe) || effect.getName().equals("with extra grenade")) {
                if(effect.getName().equals("with extra grenade")) grenader = true;
                moveMe = true;
                effectPosition = effects.indexOf(effect) + 1;
            }
        if(moveMe) {
            ArrayList<ArrayList<Integer>> box = new ArrayList<>();
            for(ArrayList<Integer> combination : combinations)
                box.add((ArrayList<Integer>)combination.clone());
            ArrayList<ArrayList<Integer>> toRemove = new ArrayList<>();
            for(ArrayList<Integer> combination : box) {
                if(!combination.contains(effectPosition)) toRemove.add(combination);
                else {
                    ArrayList<Integer> temp = (ArrayList<Integer>)combination.clone();
                    temp.remove(effectPosition);
                    combination.clear();
                    combination.add(effectPosition);
                    combination.addAll(temp);
                }
            }
            box.removeAll(toRemove);
            combinations.addAll(box);
            if(!grenader) {
                ArrayList<Integer> addendum = new ArrayList<>();
                addendum.add(1);
                addendum.add(3);
                addendum.add(2);
                combinations.add(addendum);
            }
        }
        if(constraintOrder) {
            ArrayList<ArrayList<Integer>> toRemove = new ArrayList<>();
            for(int i = 0; i < combinations.size(); i++)
                for(int j = 0; j < combinations.get(i).size() - 1; j++)
                    if(combinations.get(i).get(j+1) - combinations.get(i).get(j) > 1) {
                        toRemove.add(combinations.get(i));
                        j = combinations.get(i).size();
                    }
            combinations.removeAll(toRemove);
        }
        return combinations;
    }

    /**
     * Used to ask the message to return when using the weapon of the weapon.
     *
     * @return the message type that needs to be sent.
     */
    @Override
    public CommunicationMessage communicationMessageGenerator() {
        return CommunicationMessage.EFFECTS_LIST;
    }
}
