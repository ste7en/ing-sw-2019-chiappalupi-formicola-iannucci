package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.Direction;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.networking.utility.CommunicationMessage;

import java.util.*;
import static java.lang.Math.pow;

/**
 *
 * @author Daniele Chiappalupi
 */
public abstract class Weapon {

    protected boolean constraintOrder = false;

    /**
     * Name of the weapon
     */
    private String name;

    /**
     *  Cost of the weapon
     */
    private ArrayList<AmmoColor> cost;

    /**
     * Notes of the weapon; if the weapon doesn't have them, this attribute is null;
     */
    private String notes;

    /**
     * Boolean that saves the state of the weapon: if it's true, the weapon can shoot; otherwise, it can't
     */
    private boolean loaded;

    /**
     * List of effects of the weapon
     */
    protected ArrayList<Effect> effects;

    /**
     * String constant used in messages between client-server
     */
    public final static String weapon_key = "WEAPON";

    /**
     * Weapon's name getter
     * @return the name of the weapon
     */
    public String getName() {
        return name;
    }

    /**
     * Weapon's cost getter
     * @return the cost of the weapon
     */
    public ArrayList<AmmoColor> getCost() {
        return (ArrayList<AmmoColor>)cost.clone();
    }

    /**
     * Weapon's notes getter
     * @return the notes of the weapon (null if the weapon hasn't got them: {@link Weapon#notes})
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Weapon's state viewer
     * @return a boolean that shows if the weapon can shoot {@link Weapon#loaded}
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Unloads the weapon, setting loaded to false
     */
    public void unload() {
        loaded = false;
    }

    /**
     * Loads the weapon, setting loaded to true
     */
    public void reload() {
        loaded = true;
    }

    /**
     * Weapon's effects getter
     * @return the ArrayList of Effects of the weapon
     */
    public ArrayList<Effect> getEffects() {
        return (ArrayList<Effect>)effects.clone();
    }

    /**
     * Implemented for testing and checking purposes
     * @return the weapon in a string form
     */
    @Override
    public String toString() {
        return "Name: " + name + ";\nCost: " + cost.toString() + ";\nNotes: " + notes;
    }

    public void setConstraintOrder(boolean constraintOrder) {
        this.constraintOrder = constraintOrder;
    }

    /**
     * Abstract method that will be overrode in the child-classes: it's the method that creates the possible alternatives of damages when using an effect
     * @param shooter it's the player who is using the effect
     * @param effect it's the effect that is being used
     * @param forPotentiableWeapons it's an ArrayList containing all of the damages made by other effect (if the weapon is potentiable)
     * @param map it's the GameMap
     * @param players it's an ArrayList containing all of the Players in game
     * @return an ArrayList containing all of the different alternatives of damages that can be done
     */
    public abstract ArrayList<ArrayList<Damage>> useEffect(Player shooter, Effect effect, List<Damage> forPotentiableWeapons, GameMap map, List<Player> players);

    /**
     * This method computes the possible movements of a target when shot by a weapon that can move the target
     * @param effect it's the effect that is being used by the shooter
     * @param shooter it's the player who is shooting
     * @param target it's the target of the shot
     * @param shooterPosition it's the position from where the shooter is shooting
     * @param map it's the GameMap
     * @return an ArrayList containing all the possible cells where the target can be moved
     */
    public ArrayList<Cell> computeMovement(Effect effect, Player shooter, Player target, Cell shooterPosition, GameMap map) {
        GameMap box = (GameMap)map.clone();
        if(shooterPosition != null) box.setPlayerPosition(shooter, shooterPosition);
        ArrayList<Cell> possibleMovements = new ArrayList<>();
        if(effect.getProperties().containsKey(EffectProperty.CanMoveBefore)) {
            if (effect.getProperties().get(EffectProperty.MaxDistance) == -1) {
                recursiveMovementsCanSee(possibleMovements, effect.getProperties().get(EffectProperty.CanMoveBefore), shooter, target, box);
                if(effect.getProperties().containsKey(EffectProperty.MinDistance) && effect.getProperties().get(EffectProperty.MinDistance) > 0) {
                    ArrayList<Cell> toRemove = new ArrayList<>();
                    for(Cell cell : possibleMovements) {
                        GameMap clone = (GameMap)map.clone();
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
                getMovementsFromDirections(target, box, possibleMovements, distance);
            }
        }
        Set<Cell> set = new HashSet<>(possibleMovements);
        possibleMovements.clear();
        possibleMovements.addAll(set);
        possibleMovements.sort(Cell::compareTo);
        return possibleMovements;
    }

    /**
     * Helper method to compute the possible movements of a target when the effect can move it through a direction.
     * Static because it is used also in the Newton powerup using process.
     * @param target it's the target that can be moved.
     * @param box it's a clone of the original map.
     * @param possibleMovements it's the arrayList where the possibleMovements are stored.
     * @param distance it's the maximum distance at where the movement can be done.
     */
    public static void getMovementsFromDirections(Player target, GameMap box, List<Cell> possibleMovements, int distance) {
        for(Direction direction : Direction.values()) {
            GameMap clone = (GameMap)box.clone();
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

    /**
     * This method computes the possible movements of a target made by a weapon that has the ability to move players after the shot
     * @param movements it's the ArrayList where the possible movements are stored
     * @param distance it's the recursive counter
     * @param target it's the Player who is being shot
     * @param map it's a map (not the game one) used for the recursion
     */
    protected void recursiveMovementsEverywhere(List<Cell> movements, Integer distance, Player target, GameMap map) {
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
     * @param MaxDistance it's the MaxDistance that can be done through the movement
     */
    private void recursiveMovementsInCell(List<Cell> movements, Integer distance, Player shooter, Player target, GameMap map, Integer MaxDistance) {
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
    private void recursiveMovementsCanSee(List<Cell> movements, Integer distance, Player shooter, Player target, GameMap map) {
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
     * @param forAdditionalEffects it's an ArrayList containing all of the other damages that have been already made (null if there aren't)
     * @param map it's the GameMap
     * @param players it's an ArrayList containing all of the players in game
     * @return an ArrayList containing all of the possible solutions of damages
     */
    protected ArrayList<ArrayList<Damage>> computeDamages(Effect effect, Player shooter, List<Player> forAdditionalEffects, GameMap map, List<Player> players) {
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        ArrayList<Player> targets = generateTargetsFromDistances(effect, shooter, map, players);
        if(effect.getProperties().keySet().contains(EffectProperty.AdditionalTarget) && effect.getProperties().get(EffectProperty.AdditionalTarget) > 0)
            targets.removeAll(forAdditionalEffects);
        ArrayList<ArrayList<Player>> targetsCombination;
        if(effect.getProperties().keySet().contains(EffectProperty.AdditionalTarget) && effect.getProperties().get(EffectProperty.AdditionalTarget) < 0)
            targetsCombination = generateTargetsCombinations(effect, forAdditionalEffects, shooter, map);
        else targetsCombination = generateTargetsCombinations(effect, targets, shooter, map);
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
                        movements.put(damage.getTarget(), computeMovement(effect, shooter, damage.getTarget(), null, map));
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
     * @param forAdditionalEffects it's an ArrayList containing all of the other damages that have been already made (null if there aren't)
     * @param map it's the GameMap
     * @param players it's an ArrayList containing all of the players in game
     * @return an ArrayList containing all of the possible solutions of damages
     */
    protected ArrayList<ArrayList<Damage>> computeDamageCanMoveBefore(Effect effect, Player shooter, List<Damage> forAdditionalEffects, GameMap map, List<Player> players) {
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        ArrayList<Player> targets = new ArrayList<>();
        boolean optionalEffect = effect.getProperties().get(EffectProperty.CanMoveBefore) != -1 && forAdditionalEffects != null;
        if(optionalEffect)  {
            for(Player player : players)
                if(player != shooter && computeMovement(effect, forAdditionalEffects.get(0).getTarget(), player, forAdditionalEffects.get(0).getPosition(), map).size() != 0) targets.add(player);
        } else for(Player player : players)
            if(computeMovement(effect, shooter, player, null, map).size() != 0) targets.add(player);
        for(Player target : targets) {
            ArrayList<Cell> movements = optionalEffect ? computeMovement(effect, forAdditionalEffects.get(0).getTarget(), target, forAdditionalEffects.get(0).getPosition(), map) : computeMovement(effect, shooter, target, null, map);
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
     * @param m it's the game map, needed to find the targets positions
     * @param players it's an ArrayList containing all of the players in game
     * @return the list of possible targets
     */
    private ArrayList<Player> generateTargetsFromDistances(Effect e, Player p, GameMap m, List<Player> players) {
        ArrayList<Player> targets = new ArrayList<>();
        ArrayList<Player> targetsFromMin = new ArrayList<>();
        ArrayList<Player> targetsFromMax = new ArrayList<>();
        Set<EffectProperty> properties = e.getProperties().keySet();
        if(properties.contains(EffectProperty.MinDistance)) {
            if(e.getProperties().get(EffectProperty.MinDistance) == -1) targetsFromMin.addAll(m.getUnseenTargets(p));
            else if(e.getProperties().get(EffectProperty.MinDistance) == -2) {
                for(Player player : players)
                    if(m.getRoomFromCell(m.getPositionFromPlayer(player)) != m.getRoomFromCell(m.getCellFromPlayer(p)))
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
                            if(m.getTargetsFromDirection(p, d).contains(player))
                                box.add(player);
                Set<Player> setBox = new LinkedHashSet<>(box);
                targets.clear();
                targets.addAll(setBox);
            }
            else {
                Set<Player> setBox = new LinkedHashSet<>();
                for(Direction d : Direction.values()) setBox.addAll(m.getTargetsFromDirection(p, d));
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
     * @param map it's the GameMap
     * @return an ArrayList with all the combinations
     */
    public ArrayList<ArrayList<Player>> generateTargetsCombinations(Effect e, List<Player> p, Player shooter, GameMap map) {
        if(e.getProperties().containsKey(EffectProperty.MultipleCell)) {
            Integer multipleCell = e.getProperties().get(EffectProperty.MultipleCell);
            switch (multipleCell) {
                case 0: {
                    ArrayList<ArrayList<Player>> solutions = new ArrayList<>();
                    for (Player player : p) {
                        int exists = 0;
                        Cell cell = map.getCellFromPlayer(player);
                        for (ArrayList<Player> solution : solutions)
                            if (map.getCellFromPlayer(solution.get(0)) == cell) {
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
                        ArrayList<Cell> room = map.getRoomFromCell(map.getCellFromPlayer(player));
                        for (ArrayList<Player> solution : solutions)
                            if (map.getRoomFromCell(map.getCellFromPlayer(solution.get(0))).equals(room)) {
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
                            if(map.getTargetsFromDirection(shooter, d).containsAll(solution)) controller++;
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
                        for(Player player : solution) positions.add(map.getCellFromPlayer(player));
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
    private ArrayList<ArrayList<Player>> combinationsFromMaxPlayer(Effect e, List<Player> p) {
        HashMap<Integer, ArrayList<Player>> combinations = new HashMap<>();
        if (!e.getProperties().containsKey(EffectProperty.MaxPlayer)) throw new NullPointerException("Effect doesn't have MaxPlayer property");
        if(e.getProperties().get(EffectProperty.MaxPlayer) == -1) {
            ArrayList<ArrayList<Player>> allPlayers = new ArrayList<>();
            ArrayList<Player> playersClone = new ArrayList<>(p);
            allPlayers.add(playersClone);
            return allPlayers;
        }
        Map<Integer, ArrayList<Integer>> comb = combinationsWithLowerValues(p.size(), e.getProperties().get(EffectProperty.MaxPlayer));
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
    public Map<Integer, ArrayList<Integer>> combinationsWithLowerValues(int size, int r){
        HashMap<Integer, ArrayList<Integer>> combinations = new HashMap<>();
        for(int i = 1; i <= r; i ++) {
            Map<Integer, ArrayList<Integer>> box = combinations(size, i);
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
    private Map<Integer, ArrayList<Integer>> combinations(int size, int r) {
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
    private void recursiveCombinations(List<Integer> box, int[] arr, int[] data, int start,
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
     * Calculates the combinations of effects that can be chosen from the Player who is using the Weapon
     * @return an ArrayList of effects combinations
     */
    public ArrayList<ArrayList<Integer>> effectsCombinations() {
        throw new RuntimeException("This is not a Potentiable Weapon!");
    };

    /**
     * Used to ask the message to return when using the weapon of the weapon.
     * @return the message type that needs to be sent.
     */
    public abstract CommunicationMessage communicationMessageGenerator();
}