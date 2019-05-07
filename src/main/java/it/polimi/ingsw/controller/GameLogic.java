package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import java.util.*;

import static java.lang.Math.pow;

public class GameLogic {

    private ArrayList<Player> players;
    private Board board;
    private boolean finalFrenzy;

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void move(Player player) { }

    private void grabStuff(Player player) { }

    private void shootPeople(Player player) { }

    private void death(Player player) { }

    private void spawn(Player player) { }

    private void round(Player player) { }

    private void finalFrenzyRound(Player player) { }

    public void gameOver() { }

    public void addPlayer(Player player) { }

    /**
     * This method computes all of the possible alternatives when using a weapon: the player will choose the one he prefers from the list of options returned
     * @param shooter it's the player who is using the weapon
     * @param effect it's the effect that is being used
     * @param weapon it's the weapon that is being used
     * @param forPotentiableWeapons this List is only used when the effect is an additional one; it's the list of damages that has been already done with the other effects of the same weapon
     * @return the list of options that the player can choose
     */
    public ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, Weapon weapon, ArrayList<Damage> forPotentiableWeapons) {
        GameMap map = board.getMap();
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        switch(weapon.getType()) {
            case SimpleWeapon: {
                return computeDamages(effect, shooter, null);
            }
            case SelectableWeapon: {
                if (effect.getProperties().containsKey(EffectProperty.CanMoveBefore))
                    return computeDamageCanMoveBefore(effect, shooter, null);
                if(effect.getProperties().containsKey(EffectProperty.Hard))
                    if(effect.getProperties().get(EffectProperty.Hard) == 0) return inBarbecueMode(shooter);
                return computeDamages(effect, shooter, null);
            }
            case PotentiableWeapon: {
                if(weapon.getEffects().get(0) == effect) {
                    boolean canMoveBefore = effect.getProperties().containsKey(EffectProperty.CanMoveBefore);
                    return canMoveBefore ? computeDamageCanMoveBefore(effect, shooter, null) : computeDamages(effect, shooter, null);
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
                        return computeDamages(effect, shooter, listForAdditionalEffects);
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
                        if(effect.getProperties().get(EffectProperty.EffectOnTarget) >= 0) solutions = useEffect(forPotentiableWeapons.get(effectOnTarget).getTarget(), weapon.getEffects().get(0), weapon, null);
                        else {
                            Effect box = new Effect();
                            HashMap<EffectProperty, Integer> boxProperties = (HashMap<EffectProperty, Integer>)weapon.getEffects().get(effectOnTarget).getProperties().clone();
                            boxProperties.remove(EffectProperty.EffectOnTarget);
                            box.setProperties(boxProperties);
                            solutions = computeDamages(box, forPotentiableWeapons.get(0).getTarget(), null);
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
                    if(effect.getProperties().containsKey(EffectProperty.CanMoveBefore)) return computeDamageCanMoveBefore(effect, shooter, forPotentiableWeapons);
                    if(effect.getProperties().containsKey(EffectProperty.Hard))
                        if(effect.getProperties().get(EffectProperty.Hard) == 2) {
                            if(forPotentiableWeapons == null) throw new NullPointerException("This effects needs the history of the damages made by the other effects to work!");
                            if(forPotentiableWeapons.size() == 0) throw new RuntimeException("The history of damages made by the other effects can't be empty!");
                            return withTurretTripod(shooter, forPotentiableWeapons);
                        }
                    return computeDamages(effect, shooter, null);
                }
            }
        }
        return solutions;
    }

    /**
     * This method computes the possible movements of a target when shot by a weapon that can move the target
     * @param effect it's the effect that is being used by the shooter
     * @param shooter it's the player who is shooting
     * @param target it's the target of the shot
     * @return an ArrayList containing all the possible cells where the target can be moved
     */
    public ArrayList<Cell> computeMovement(Effect effect, Player shooter, Player target, Cell shooterPosition) {
        GameMap box = (GameMap)board.getMap().clone();
        if(shooterPosition != null) box.setPlayerPosition(shooter, shooterPosition);
        ArrayList<Cell> possibleMovements = new ArrayList<>();
        if(effect.getProperties().containsKey(EffectProperty.CanMoveBefore)) {
            if (effect.getProperties().get(EffectProperty.MaxDistance) == -1) {
                recursiveMovementsCanSee(possibleMovements, effect.getProperties().get(EffectProperty.CanMoveBefore), shooter, target, box);
                if(effect.getProperties().containsKey(EffectProperty.MinDistance) && effect.getProperties().get(EffectProperty.MinDistance) > 0) {
                    ArrayList<Cell> toRemove = new ArrayList<>();
                    for(Cell cell : possibleMovements) {
                        GameMap clone = (GameMap)board.getMap().clone();
                        clone.setPlayerPosition(target, cell);
                        if(!clone.getTargetsAtMinDistance(shooter, effect.getProperties().get(EffectProperty.MinDistance)).contains(target))
                            toRemove.add(cell);
                    }
                    possibleMovements.removeAll(toRemove);
                }
            }
            else
                recursiveMovementsInCell(possibleMovements, effect.getProperties().get(EffectProperty.CanMoveBefore), shooter, target, box, effect.getProperties().get(EffectProperty.MaxDistance));
        } else {
            if(effect.getProperties().get(EffectProperty.CanMoveAfter) > 0) recursiveMovementsEverywhere(possibleMovements, effect.getProperties().get(EffectProperty.CanMoveAfter), target, box);
            else {
                int distance = effect.getProperties().get(EffectProperty.CanMoveAfter) * (-1);
                for(Direction direction : Direction.values()) {
                    GameMap clone = (GameMap)board.getMap().clone();
                    ArrayList<Cell> movementsInDirection = new ArrayList<>();
                    int distanceBox = distance;
                    while(distanceBox > 0) {
                        if(clone.isAOneStepValidMove(target, clone.getCellFromDirection(clone.getCellFromPlayer(target), direction))) {
                            Cell cell = clone.getCellFromDirection(clone.getCellFromPlayer(target), direction);
                            movementsInDirection.add(cell);
                            clone.setPlayerPosition(target, cell);
                            distanceBox--;
                        } else distanceBox = -1;
                    }
                    possibleMovements.addAll(movementsInDirection);
                    possibleMovements.add(box.getCellFromPlayer(target));
                }
            }
        }
        Set<Cell> set = new HashSet<>(possibleMovements);
        possibleMovements.clear();
        possibleMovements.addAll(set);
        possibleMovements.sort(Cell::compareTo);
        return possibleMovements;
    }

    /**
     * This method computes the possible movements of a target made by a weapon that has the ability to move players after the shot
     * @param movements it's the ArrayList where the possible movements are stored
     * @param distance it's the recursive counter
     * @param target it's the Player who is being shot
     * @param map it's a map (not the game one) used for the recursion
     */
    private void recursiveMovementsEverywhere (ArrayList<Cell> movements, Integer distance, Player target, GameMap map) {
        if(distance == 0) {
                movements.add(map.getCellFromPlayer(target));
                return;
        }
        movements.add(map.getCellFromPlayer(target));
        for(Direction direction : Direction.values()) {
            Cell targetPosition = map.getPositionFromPlayer(target);
            Cell cell = map.getCellFromDirection(targetPosition, direction);
            if(cell != null && map.isAOneStepValidMove(target, cell)) {
                GameMap box = (GameMap)map.clone();
                box.setPlayerPosition(target, cell);
                recursiveMovementsEverywhere(movements, distance-1, target, box);
            }
        }
    }

    /**
     * This method computes the possible movements of a target made by a weapon that has the ability to move players
     * @param movements it's the ArrayList where the possible movements are stored
     * @param distance it's the recursive counter
     * @param shooter it's the Player who is using the weapon
     * @param target it's the Player who is being shot
     * @param map it's a map (not the game one) used for the recursion
     */
    private void recursiveMovementsInCell(ArrayList<Cell> movements, Integer distance, Player shooter, Player target, GameMap map, Integer MaxDistance) {
        if(distance == 0) {
            if(map.getTargetsAtMaxDistance(shooter, MaxDistance).contains(target))
                movements.add(map.getCellFromPlayer(target));
            return;
        }
        if(map.getTargetsAtMaxDistance(shooter, MaxDistance).contains(target)) movements.add(map.getCellFromPlayer(target));
        for(Direction direction : Direction.values()) {
            GameMap box = (GameMap)map.clone();
            Cell targetPosition = map.getPositionFromPlayer(target);
            Cell cell = map.getCellFromDirection(targetPosition, direction);
            if(cell != null && map.isAOneStepValidMove(target, cell)) {
                box.setPlayerPosition(target, cell);
                recursiveMovementsInCell(movements, distance-1, shooter, target, box, MaxDistance);
            }
        }
        return;
    }

    /**
     * This method computes the possible movements of a target made by a weapon that has the ability to move players
     * @param movements it's the ArrayList where the possible movements are stored
     * @param distance it's the recursive counter
     * @param shooter it's the Player who is using the weapon
     * @param target it's the Player who is being shot
     * @param map it's a map (not the game one) used for the recursion
     */
    private void recursiveMovementsCanSee(ArrayList<Cell> movements, Integer distance, Player shooter, Player target, GameMap map) {
        if(distance == 0) {
            if(map.getSeenTargets(shooter).contains(target))
                movements.add(map.getCellFromPlayer(target));
            return;
        }
        if(map.getSeenTargets(shooter).contains(target)) movements.add(map.getCellFromPlayer(target));
        for(Direction direction : Direction.values()) {
            GameMap box = (GameMap)map.clone();
            Cell targetPosition = map.getPositionFromPlayer(target);
            Cell cell = map.getCellFromDirection(targetPosition, direction);
            if(cell != null && map.isAOneStepValidMove(target, cell)) {
                box.setPlayerPosition(target, cell);
                recursiveMovementsCanSee(movements, distance-1, shooter, target, box);
            }
        }
    }

    /**
     * Computes the ArrayList of damages from the effect and the damage when the former only depends from distances and max number of players
     * @param effect the effect that is being used
     * @param shooter the player who is using the effect
     * @return an ArrayList containing all of the possible solutions of damages
     */
    private ArrayList<ArrayList<Damage>> computeDamages(Effect effect, Player shooter, ArrayList<Player> forAdditionalEffects) {
        GameMap map = board.getMap();
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        ArrayList<Player> targets = generateTargetsFromDistances(effect, shooter, map);
        if(effect.getProperties().keySet().contains(EffectProperty.AdditionalTarget) && effect.getProperties().get(EffectProperty.AdditionalTarget) > 0)
            targets.removeAll(forAdditionalEffects);
        ArrayList<ArrayList<Player>> targetsCombination;
        if(effect.getProperties().keySet().contains(EffectProperty.AdditionalTarget) && effect.getProperties().get(EffectProperty.AdditionalTarget) < 0)
            targetsCombination = generateTargetsCombinations(effect, forAdditionalEffects, shooter);
        else targetsCombination = generateTargetsCombinations(effect, targets, shooter);
        Damage d;
        ArrayList<Damage> arrayD;
        for(int i = 0; i < targetsCombination.size(); i++) {
            solutions.add(new ArrayList<>());
            arrayD = solutions.get(i);
            for(int k = 0; k < targetsCombination.get(i).size(); k++) {
                arrayD.add(new Damage());
                d = arrayD.get(k);
                d.setTarget(targetsCombination.get(i).get(k));
                if(effect.getProperties().containsKey(EffectProperty.Damage)) d.setDamage(effect.getProperties().get(EffectProperty.Damage));
                if(effect.getProperties().containsKey(EffectProperty.Mark)) {
                    int marks = effect.getProperties().get(EffectProperty.Mark);
                    if(marks < 0 && k == 0) {
                        int positiveMarks = (-1) * marks;
                        d.setMarks(positiveMarks);
                        ArrayList<Player> markedPlayers = map.getTargetsInMyCell(d.getTarget());
                        for(Player p : markedPlayers) {
                            arrayD.add(new Damage());
                            arrayD.get(arrayD.size() - 1).setTarget(p);
                            arrayD.get(arrayD.size() - 1).setMarks(positiveMarks);
                        }
                    }
                    else if(marks != -1) d.setMarks(marks);
                }
            }
        }
        if(effect.getProperties().containsKey(EffectProperty.CanMoveAfter)) {
            ArrayList<ArrayList<Damage>> solutionsBox = new ArrayList<>();
            HashMap<Player, ArrayList<Cell>> movements = new HashMap<>();
            for (ArrayList<Damage> damages : solutions) {
                Damage temp = damages.get(0);
                for (Damage damage : damages)
                    if (!movements.containsKey(damage.getTarget()))
                        movements.put(damage.getTarget(), computeMovement(effect, shooter, damage.getTarget(), null));
                    Player target = temp.getTarget();
                    for (Cell cell : movements.get(target)) {
                        ArrayList<Damage> box = new ArrayList<>();
                        Damage damageBox = new Damage();
                        damageBox.setMarks(temp.getMarks());
                        damageBox.setTarget(temp.getTarget());
                        damageBox.setDamage(temp.getDamage());
                        damageBox.setPosition(cell);
                        box.add(damageBox);
                        solutionsBox.add(box);
                    }
                }
            solutionsBox.sort(Comparator.comparing(a -> a.get(0)));
            solutions = solutionsBox;
        }
        if(effect.getProperties().containsKey(EffectProperty.MoveMe) && effect.getProperties().get(EffectProperty.MoveMe) < 0) {
            int movements = effect.getProperties().get(EffectProperty.MoveMe);
            if(movements == -1)
                for(ArrayList<Damage> damages : solutions) {
                    Damage damage = new Damage();
                    damage.setTarget(shooter);
                    damage.setPosition(map.getCellFromPlayer(damages.get(0).getTarget()));
                    damages.add(damage);
                }
            else if (movements == -2) {
                ArrayList<ArrayList<Damage>> toRemove = new ArrayList<>();
                for(ArrayList<Damage> damages : solutions) {
                    if(damages.size() == 1 && map.getTargetsAtMinDistance(shooter, 2).contains(damages.get(0).getTarget()))
                        toRemove.add(damages);
                    else {
                        Damage damage = new Damage();
                        damage.setTarget(shooter);
                        Player target;
                        if(damages.size() == 1) target = damages.get(0).getTarget();
                        else target = map.getTargetsAtMinDistance(shooter, 2).contains(damages.get(0).getTarget()) ? damages.get(0).getTarget() : damages.get(1).getTarget();
                        damage.setPosition(map.getCellFromPlayer(target));
                        damages.add(damage);
                    }
                }
                solutions.removeAll(toRemove);
            }
        }
        ArrayList<ArrayList<Damage>> emptyRemover = new ArrayList<>();
        for(ArrayList<Damage> solution : solutions) if(solution.size() == 0) emptyRemover.add(solution);
        solutions.removeAll(emptyRemover);
        return solutions;
    }

    /**
     * Computes the ArrayList of damages when the effect that is being used has the ability to move players before attacking them
     * @param effect it's the effect that is being used
     * @param shooter it's the player who is using the effect
     * @return an ArrayList containing all of the possible solutions of damages
     */
    private ArrayList<ArrayList<Damage>> computeDamageCanMoveBefore(Effect effect, Player shooter, ArrayList<Damage> forAdditionalEffects) {
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        ArrayList<Player> targets = new ArrayList<>();
        boolean optionalEffect = effect.getProperties().get(EffectProperty.CanMoveBefore) != -1 && forAdditionalEffects != null;
        if(optionalEffect)  {
            for(Player player : players)
                if(player != shooter && computeMovement(effect, forAdditionalEffects.get(0).getTarget(), player, forAdditionalEffects.get(0).getPosition()).size() != 0) targets.add(player);
        } else for(Player player : players)
            if(computeMovement(effect, shooter, player, null).size() != 0) targets.add(player);
        for(Player target : targets) {
            ArrayList<Cell> movements = optionalEffect ? computeMovement(effect, forAdditionalEffects.get(0).getTarget(), target, forAdditionalEffects.get(0).getPosition()) : computeMovement(effect, shooter, target, null);
            ArrayList<ArrayList<Damage>> solutionsBox = new ArrayList<>();
            for(int i = 0; i < movements.size(); i++) {
                solutionsBox.add(new ArrayList<>());
                solutionsBox.get(i).add(new Damage());
                solutionsBox.get(i).get(0).setTarget(target);
                solutionsBox.get(i).get(0).setDamage(effect.getProperties().get(EffectProperty.Damage));
                if(effect.getProperties().containsKey(EffectProperty.Mark)) solutionsBox.get(i).get(0).setMarks(effect.getProperties().get(EffectProperty.Mark));
                    solutionsBox.get(i).get(0).setPosition(movements.get(i));
                }
            solutions.addAll(solutionsBox);
            }
        return solutions;
    }

    /**
     * Generates the list of possible targets from the MinDistance and MaxDistance parameters of the Effect
     * @param e it's the effect of analysis
     * @param p it's the player who is using the effect
     * @param m it's the game map, needed to find the targets' positions
     * @return the list of possible targets
     */
    private ArrayList<Player> generateTargetsFromDistances(Effect e, Player p, GameMap m) {
        ArrayList<Player> targets = new ArrayList<>();
        ArrayList<Player> targetsFromMin = new ArrayList<>();
        ArrayList<Player> targetsFromMax = new ArrayList<>();
        Set<EffectProperty> properties = e.getProperties().keySet();
        if(properties.contains(EffectProperty.MinDistance)) {
            if(e.getProperties().get(EffectProperty.MinDistance) == -1) targetsFromMin.addAll(m.getUnseenTargets(p));
            else if(e.getProperties().get(EffectProperty.MinDistance) == -2) {
                GameMap map = board.getMap();
                for(Player player : players)
                    if(map.getRoomFromCell(map.getPositionFromPlayer(player)) != map.getRoomFromCell(map.getCellFromPlayer(p)))
                        targetsFromMin.add(player);
            }
            else targetsFromMin.addAll(m.getTargetsAtMinDistance(p, e.getProperties().get(EffectProperty.MinDistance)));
        }
        if(properties.contains(EffectProperty.MaxDistance)) {
            if(e.getProperties().get(EffectProperty.MaxDistance) == -1) targetsFromMax.addAll(m.getSeenTargets(p));
            else targetsFromMax.addAll(m.getTargetsAtMaxDistance(p, e.getProperties().get(EffectProperty.MaxDistance)));
        }
        if(properties.contains(EffectProperty.MinDistance) && properties.contains(EffectProperty.MaxDistance)) {
            for (Player player : players)
                if (targetsFromMin.contains(player) && targetsFromMax.contains(player)) targets.add(player);
        }
        else if(properties.contains(EffectProperty.MinDistance)) targets = targetsFromMin;
        else targets = targetsFromMax;

        if(properties.contains(EffectProperty.MultipleCell) && e.getProperties().get(EffectProperty.MultipleCell) == -2) {
            ArrayList<Player> box = new ArrayList<>();
            if(properties.contains(EffectProperty.MaxDistance)) {
                for(Player player : players)
                    if(targets.contains(player))
                        for(Direction d : Direction.values())
                            if(board.getMap().getTargetsFromDirection(p, d).contains(player))
                                box.add(player);
            Set<Player> setBox = new LinkedHashSet<>(box);
            targets.clear();
            targets.addAll(setBox);
            }
            else {
                Set<Player> setBox = new LinkedHashSet<>();
                for(Direction d : Direction.values()) setBox.addAll(board.getMap().getTargetsFromDirection(p, d));
                box.addAll(setBox);
                targets = box;
            }
        }
        return targets;
    }

    /**
     * Generates all of the possible targets combinations while using an effect (public for testing purposes)
     * @param e it's the effect of analysis
     * @param p it's the ArrayList the potential targets
     * @param shooter it's the player who is using the weapon
     * @return an ArrayList with all the combinations
     */
    public ArrayList<ArrayList<Player>> generateTargetsCombinations(Effect e, ArrayList<Player> p, Player shooter) {
        if(e.getProperties().containsKey(EffectProperty.MultipleCell)) {
            Integer multipleCell = e.getProperties().get(EffectProperty.MultipleCell);
            switch (multipleCell) {
                case 0: {
                    ArrayList<ArrayList<Player>> solutions = new ArrayList<>();
                    for (Player player : p) {
                        int exists = 0;
                        Cell cell = board.getMap().getCellFromPlayer(player);
                        for (ArrayList<Player> solution : solutions)
                            if (board.getMap().getCellFromPlayer(solution.get(0)) == cell) {
                                solution.add(player);
                                exists = 1;
                            }
                        if(exists == 0) {
                            solutions.add(new ArrayList<>());
                            solutions.get(solutions.size() - 1).add(player);
                        }
                    }
                    return solutions;
                }
                case -1: {
                    ArrayList<ArrayList<Player>> solutions = new ArrayList<>();
                    for (Player player : p) {
                        int exists = 0;
                        ArrayList<Cell> room = board.getMap().getRoomFromCell(board.getMap().getCellFromPlayer(player));
                        for (ArrayList<Player> solution : solutions)
                            if (board.getMap().getRoomFromCell(board.getMap().getCellFromPlayer(solution.get(0))).equals(room)) {
                                solution.add(player);
                                exists = 1;
                            }
                        if(exists == 0) {
                            solutions.add(new ArrayList<>());
                            solutions.get(solutions.size() - 1).add(player);
                        }
                    }
                    return solutions;
                }
                case -2: {
                    ArrayList<ArrayList<Player>> solutions = combinationsFromMaxPlayer(e, p);
                    ArrayList<ArrayList<Player>> toRemove = new ArrayList<>();
                    for(ArrayList<Player> solution : solutions) {
                        int controller = 0;
                        for(Direction d : Direction.values())
                            if(board.getMap().getTargetsFromDirection(shooter, d).containsAll(solution)) controller++;
                        if(controller == 0) toRemove.add(solution);
                    }
                    solutions.removeAll(toRemove);
                    return solutions;
                }
                default: {
                    ArrayList<ArrayList<Player>> solutions = combinationsFromMaxPlayer(e, p);
                    ArrayList<ArrayList<Player>> toRemove = new ArrayList<>();
                    for(ArrayList<Player> solution : solutions) {
                        Set<Cell> positions = new HashSet<>();
                        for(Player player : solution) positions.add(board.getMap().getCellFromPlayer(player));
                        if(positions.size() != solution.size()) toRemove.add(solution);
                    }
                    solutions.removeAll(toRemove);
                    return solutions;
                }
            }
        }
        return combinationsFromMaxPlayer(e, p);
    }

    /**
     * Generates the Players combinations from the list of targets and the effect properties
     * @param e it's the effect that is being used
     * @param p it's the list of players on who the effect can be used
     * @return an ArratList containing all of the possible combinations of those targets
     */
    private ArrayList<ArrayList<Player>> combinationsFromMaxPlayer(Effect e, ArrayList<Player> p) {
        HashMap<Integer, ArrayList<Player>> combinations = new HashMap<>();
        if (!e.getProperties().containsKey(EffectProperty.MaxPlayer)) throw new NullPointerException("Effect doesn't have MaxPlayer property");
        if(e.getProperties().get(EffectProperty.MaxPlayer) == -1) {
            ArrayList<ArrayList<Player>> allPlayers = new ArrayList<>();
            allPlayers.add((ArrayList<Player>)p.clone());
            return allPlayers;
        }
        HashMap<Integer, ArrayList<Integer>> comb = combinationsWithLowerValues(p.size(), e.getProperties().get(EffectProperty.MaxPlayer));
        for(int i = 0; i < comb.keySet().size(); i++) {
            combinations.put(i, new ArrayList<>());
            for(int k = 0; k < comb.get(i).size(); k++)
                combinations.get(i).add(p.get(comb.get(i).get(k) - 1));
        }
        ArrayList<ArrayList<Player>> combs = new ArrayList<>();
        for(Integer i : combinations.keySet())
            combs.add(i, combinations.get(i));
        return combs;
    }

    /**
     * Returns the mathematical combination of a set of Integer, including all of the possible lenghts from zero to the max size (public for testing purposes)
     * @param size size of the set to combine
     * @param r size of the biggest combinations to generate
     * @return a HashMap containing the combinations and their indexes
     */
    public HashMap<Integer, ArrayList<Integer>> combinationsWithLowerValues(int size, int r){
        HashMap<Integer, ArrayList<Integer>> combinations = new HashMap<>();
        for(int i = 1; i <= r; i ++) {
            HashMap<Integer, ArrayList<Integer>> box = combinations(size, i);
            int k = 0;
            for(Integer j : combinations.keySet()) if(j > k) k = j;
            if(k != 0) k++;
            for(Integer j : box.keySet()) combinations.put(j+k, box.get(j));
        }
        return combinations;
    }

    /**
     * Returns the mathematical combination of a set of Integer: it's used to generate all the possible combinations of targets
     * @param size size of the set to combine
     * @param r size of the combinations to generate
     * @return a HashMap containing the combinations and their indexes
     */
    private HashMap<Integer, ArrayList<Integer>> combinations(int size, int r) {
        int[] i = new int[size];
        ArrayList<Integer> param = new ArrayList<>();
        for(int k=0; k < size; k++) i[k] = k+1;
        recursiveCombinations(param, i, new int[size], 0, size-1, 0, r);
        HashMap<Integer, ArrayList<Integer>> combs = new HashMap<>();

        for(int j = 0; j < param.size(); j++) {
            combs.put(j, new ArrayList<>());
            int x = 10;
            while(param.get(j)>=x) x*=10;
            x /= 10;
            int cifra=0;
            while(x>=1)
            {
                cifra = param.get(j)/x;
                combs.get(j).add(cifra);
                int box = param.get(j) - cifra*x;
                param.remove(j);
                param.add(j, box);
                x /= 10;
            }
        }

        return combs;
    }

    /**
     * Recursive function to compute the combinations
     * @param box it's where the combinations will be stored
     * @param arr it's the array where the numbers to be combined are
     * @param data it's a temporary array where to store the computations
     * @param start saves the start of the computation (needed because of recursion)
     * @param end saves the end of the computation
     * @param index saves the state of the computation (needed because of recursion)
     * @param r saves the size of the combinations
     */
    private void recursiveCombinations(ArrayList<Integer> box, int[] arr, int[] data, int start,
                                       int end, int index, int r) {
        if (index == r)
        {
            int a = 0;
            for (int j=0; j<r; j++)
                a += pow(10, r-1-j) * data[j];
            box.add(a);
            return;
        }
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            recursiveCombinations(box, arr, data, i+1, end, index+1, r);
        }
    }

    /**
     * This method computes the different solutions of damages when using the "in Barbecue Mode" effect, of the "Flamethrower" weapon
     * @param shooter it's the Player who is using the effect
     * @return an ArrayList containing all of the different solutions
     */
    private ArrayList<ArrayList<Damage>> inBarbecueMode(Player shooter) {
        ArrayList<ArrayList<Damage>> damages = new ArrayList<>();
        for(Direction direction : Direction.values())
            if(board.getMap().getTargetsFromDirection(shooter, direction).size() > 0) {
                ArrayList<Damage> arrayD = new ArrayList<>();
                damages.add(arrayD);
                ArrayList<Player> targetsInDirection = board.getMap().getTargetsFromDirection(shooter, direction);
                targetsInDirection.removeAll(board.getMap().getTargetsInMyCell(shooter));
                for(Player player : targetsInDirection) {
                    Damage damage = new Damage();
                    damage.setTarget(player);
                    if(board.getMap().getTargetsAtMaxDistance(shooter, 1).contains(player)) {
                        damage.setDamage(2);
                        arrayD.add(damage);
                    } else if(board.getMap().getTargetsAtMaxDistance(shooter, 2).contains(player)) {
                        damage.setDamage(1);
                        arrayD.add(damage);
                    }
                }
                if(arrayD.size() == 0) damages.remove(arrayD);
            }
        return damages;
    }

    private ArrayList<ArrayList<Damage>> withTurretTripod (Player shooter, ArrayList<Damage> earlyDamages) {
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
        solutions = computeDamages(boxEffect, shooter, null);
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