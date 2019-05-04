package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    public ArrayList<ArrayList<Damage>> useEffect(Player player, Effect effect, Weapon weapon) {
        GameMap map = board.getMap();
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        switch(weapon.getType()) {
            case SimpleWeapon: {
                return computeDamages(effect, player);
            }
            case SelectableWeapon: {
                if (effect.getProperties().containsKey(EffectProperty.CanMoveBefore)) {
                    //TODO: Which Player do you wanna use this effect on?
                }
                solutions = computeDamages(effect, player);
                return solutions;
            }
            case PotentiableWeapon: {
                //TODO
            }
        }
        return solutions;
    }

    public ArrayList<Cell> computeMovement(Effect effect, Player shooter, Player target) {
        GameMap box = new GameMap(board.getMap().getMapType(), board.getMap().getPlayersPosition());
        ArrayList<Cell> possibleMovements = new ArrayList<>();
        recursiveMovements(possibleMovements, effect.getProperties().get(EffectProperty.CanMoveBefore), shooter, target, box);
        Set<Cell> set = new HashSet<>(possibleMovements);
        possibleMovements.clear();
        possibleMovements.addAll(set);
        return possibleMovements;
    }

    /**
     * This method computes the possible movements of a target made by a weapon that has the ability to move players
     * @param movements it's the ArrayList where the possible movements are stored
     * @param distance it's the recursive counter
     * @param shooter it's the Player who is using the weapon
     * @param target it's the Player who is being shot
     * @param map it's a map (not the game one) used for the recursion
     */
    private void recursiveMovements(ArrayList<Cell> movements, Integer distance, Player shooter, Player target, GameMap map) {
        if(distance == 0 && map.getSeenTargets(shooter).contains(target)) {
            movements.add(map.getCellFromPlayer(target));
            return;
        }
        if(map.getSeenTargets(shooter).contains(target)) movements.add(map.getCellFromPlayer(target));
        for(Direction direction : Direction.values()) {
            GameMap box = new GameMap(map.getMapType(), map.getPlayersPosition());
            Cell targetPosition = map.getPositionFromPlayer(target);
            Cell cell = map.getCellFromDirection(targetPosition, direction);
            if(cell != null) {
                box.setPlayerPosition(target, cell);
                recursiveMovements(movements, distance-1, shooter, target, box);
                return;
            }
            return;
        }
    }

    /**
     * Computes the ArrayList of damages from the effect and the damage when the former only depends from distances and max number of players
     * @param effect the effect that is been using
     * @param player the player who is using the effect
     * @return an ArrayList containing all of the possible solutions of damages
     */
    private ArrayList<ArrayList<Damage>> computeDamages(Effect effect, Player player) {
        GameMap map = board.getMap();
        ArrayList<ArrayList<Damage>> solutions = new ArrayList<>();
        ArrayList<Player> targets = generateTargetsFromDistances(effect, player, map);
        ArrayList<ArrayList<Player>> targetsCombination = generateTargetsCombinations(effect, targets);
        Damage d;
        ArrayList<Damage> arrayD;
        for(int i = 0; i < targetsCombination.size(); i++) {
            solutions.add(new ArrayList());
            arrayD = solutions.get(i);
            for(int k = 0; k < targetsCombination.get(i).size(); k++) {
                arrayD.add(new Damage());
                d = arrayD.get(k);
                d.setTarget(targetsCombination.get(i).get(k));
                d.setDamage(effect.getProperties().get(EffectProperty.Damage));
                if(effect.getProperties().containsKey(EffectProperty.Mark)) d.setMarks(effect.getProperties().get(EffectProperty.Mark));
            }
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
            else targetsFromMin.addAll(m.getTargetsAtMinDistance(p, e.getProperties().get(EffectProperty.MinDistance)));
        }
        if(properties.contains(EffectProperty.MaxDistance)) {
            if(e.getProperties().get(EffectProperty.MaxDistance) == -1) targetsFromMax.addAll(m.getSeenTargets(p));
            else targetsFromMax.addAll(m.getTargetsAtMaxDistance(p, e.getProperties().get(EffectProperty.MaxDistance)));
        }
        if(properties.contains(EffectProperty.MinDistance) && properties.contains(EffectProperty.MaxDistance)) {
            for (Player player : players)
                if (targetsFromMin.contains(player) && targetsFromMax.contains(player)) targets.add(player);
            return targets;
        }
        else if(properties.contains(EffectProperty.MinDistance)) return targetsFromMin;
        return targetsFromMax;
    }

    /**
     * Generates all of the possible targets combinations while using an effect
     * @param e it's the effect of analysis
     * @param p it's the ArrayList the potential targets
     * @return an ArrayList with all the combinations
     */
    public ArrayList<ArrayList<Player>> generateTargetsCombinations(Effect e, ArrayList<Player> p) {
        HashMap<Integer, ArrayList<Player>> combinations = new HashMap<>();
        if(e.getProperties().containsKey(EffectProperty.MaxPlayer)) {
            if(e.getProperties().get(EffectProperty.MaxPlayer) == -1) {
                ArrayList<ArrayList<Player>> allPlayers = new ArrayList<>();
                allPlayers.add((ArrayList<Player>)p.clone());
                return allPlayers;
            }
            HashMap<Integer, ArrayList<Integer>> comb = combinations(p.size(), e.getProperties().get(EffectProperty.MaxPlayer));
            for(int i = 0; i < comb.keySet().size(); i++) {
                combinations.put(i, new ArrayList<>());
                for(int k = 0; k < comb.get(i).size(); k++)
                    combinations.get(i).add(p.get(comb.get(i).get(k) - 1));
            }
        }
        ArrayList<ArrayList<Player>> combs = new ArrayList<>();
        for(Integer i : combinations.keySet())
            combs.add(i, combinations.get(i));
        return combs;
    }

    /**
     * Returns the mathematical combination of a set of Integer: it's used to generate all the possible combinations of targets
     * @param size size of the set to combine
     * @param r size of the combinations to generate
     * @return a HashMap containing the combinations and their indexes
     */
    public HashMap<Integer, ArrayList<Integer>> combinations(int size, int r) {
        int[] i = new int[size];
        ArrayList<Integer> param = new ArrayList<Integer>();
        for(int k=0; k < size; k++) i[k] = k+1;
        numberCombination(param, i, new int[size], 0, size-1, 0, r);
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
    private void numberCombination(ArrayList<Integer> box, int arr[], int data[], int start,
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
            numberCombination(box, arr, data, i+1, end, index+1, r);
        }
    }

}