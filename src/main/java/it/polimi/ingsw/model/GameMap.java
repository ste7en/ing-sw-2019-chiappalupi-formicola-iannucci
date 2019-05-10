package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Elena Iannucci
 */

public class GameMap {

    private Cell[][] map;
    private LinkedHashMap<Player, Cell> playersPosition;
    private int rows;
    private int columns;
    private MapType mapType;

    public GameMap(MapType mapType, LinkedHashMap<Player, Cell> playersPosition){
        this.mapType = mapType;
        rows=3;
        columns=4;
        this.playersPosition = new LinkedHashMap<>();
        this.playersPosition.putAll(playersPosition);
        this.map = new Cell[3][4];
        //tem
        switch(mapType){
            case conf_4:
            map[0][0] = new Cell(Border.wall, Border.door, Border.space, Border.wall, CellColor.red,false , null, 0, 0 );
            map[0][1] = new Cell(Border.wall, Border.space, Border.door, Border.door, CellColor.blue,false , null, 0, 1 );
            map[0][2] = new Cell(Border.wall, Border.door, Border.door, Border.space, CellColor.blue, true, null, 0, 2 );
            map[0][3] = new Cell(Border.wall, Border.wall, Border.door, Border.door, CellColor.green,false , null, 0, 3 );
            map[1][0] = new Cell(Border.space, Border.wall, Border.door, Border.wall, CellColor.red,true , null, 1, 0 );
            map[1][1] = new Cell(Border.door, Border.wall, Border.door, Border.wall, CellColor.pink,false , null, 1, 1 );
            map[1][2] = new Cell(Border.door, Border.space, Border.space, Border.wall, CellColor.yellow,false , null, 1, 2 );
            map[1][3] = new Cell(Border.door, Border.wall, Border.space, Border.space, CellColor.yellow,false , null, 1, 3 );
            map[2][0] = new Cell(Border.door, Border.space, Border.wall, Border.wall, CellColor.white,false , null, 2, 0 );
            map[2][1] = new Cell(Border.door, Border.door, Border.wall, Border.space, CellColor.white,false , null, 2, 1 );
            map[2][2] = new Cell(Border.space, Border.space, Border.wall, Border.door, CellColor.yellow,false , null, 2, 2 );
            map[2][3] = new Cell(Border.space, Border.wall, Border.wall, Border.space, CellColor.yellow,true , null, 2, 3 );
        }
    }

    public GameMap(Cell[][] map, HashMap<Player, Cell> playersPosition, int rows, int columns){
        this.playersPosition = new LinkedHashMap<>();
        int i,j;
        for (i=0; i<rows; i++) {
            for (j = 0; j < columns; j++) {
                this.map[i][j] = map[i][j];
            }
        }
        this.playersPosition.putAll(playersPosition);
        this.rows=rows;
        this.columns=columns;
    }

    @Override
    public Object clone() {
        GameMap clone = new GameMap(this.mapType, (LinkedHashMap<Player, Cell>)this.playersPosition.clone());
        clone.map = this.map.clone();
        return clone;
    }

    public Cell getCell(int row, int column) {
        return map[row][column];
    }

    public Cell getCellFromPlayer(Player player){
        return playersPosition.get(player);
    }

    public ArrayList<Cell> getRoomFromCell(Cell cell) {
        ArrayList<Cell> room = new ArrayList<>();
        int i,j;
        for (i=0; i<rows; i++) {
            for(j=0; j<columns; j++){
                if (map[i][j].getColor().equals(cell.getColor())) room.add(map[i][j]);
            }
        }
        return room;
    }

    public Cell getCellFromDirection(Cell cell, Direction direction){
        int i,j;
        for (i=0; i<rows; i++)
            for(j=0; j<columns; j++)
                if (map[i][j].equals(cell)) {
                    if (direction == Direction.North) {
                        if (i > 0)  return map[i - 1][j];
                    }
                    else if (direction == Direction.East) {
                        if (j < 3)  return map[i][j + 1];
                    }
                    else if (direction == Direction.South) {
                        if (i < 2)  return map[i + 1][j];
                    }
                    else if (direction == Direction.West) {
                        if (j > 0)  return map[i][j - 1];
                    }
                }
        return null;
    }

    private ArrayList<Player> getPlayersFromCell(Cell cell){
        ArrayList<Player> playersInThatCell = new ArrayList<>();
        for (Player player : playersPosition.keySet()){
            if (playersPosition.get(player).equals(cell)) playersInThatCell.add(player);
        }
        return playersInThatCell;
    }

    public ArrayList<Player> getTargetsInMyCell(Player player){
        ArrayList<Player> targets = getPlayersFromCell(playersPosition.get(player));
        targets.remove(player);
        return targets;
    }

    public ArrayList<Player> getTargetsAtMaxDistance(Player player, int distance){
        ArrayList<Player> targets = getTargetsAtMaxDistanceHelper(getCellFromPlayer(player), distance);
        targets.remove(player);
        return targets;
    }

    private ArrayList<Player> getTargetsAtMaxDistanceHelper(Cell cell, int distance){
        ArrayList<Player> targets = new ArrayList<>();
        if (distance==0){
            targets.addAll(getPlayersFromCell(cell));
            return targets;
        }
        for (Direction direction : Direction.values()) {
            if ((getCellFromDirection(cell, direction) != null) && (cell.adiacency(direction) != Border.wall)) {
                targets.addAll(getTargetsAtMaxDistanceHelper(getCellFromDirection(cell, direction), distance - 1));
            }
        }
        targets.addAll(getPlayersFromCell(cell));
        targets.addAll(getAdiacentTargets(cell));
        Set<Player> duplicatesEliminator = new LinkedHashSet<>();
        duplicatesEliminator.addAll(targets);
        targets.clear();
        targets.addAll(duplicatesEliminator);
        targets.sort(Player::compareTo);
        return targets;
    }

    public ArrayList<Player> getTargetsAtMinDistance(Player player, int distance){
        ArrayList<Player> targets = new ArrayList<>();
        for(Player tempPlayer : playersPosition.keySet() ){
            if (distance>0 && !getTargetsAtMaxDistance(player,distance-1).contains(tempPlayer) && tempPlayer!= player) {
                targets.add(tempPlayer);
            }
            else if(distance==0 && tempPlayer != player) {
                targets.add(tempPlayer);
            }
        }
        targets.sort(Player::compareTo);
        return targets;
    }

    public ArrayList<Player> getAdiacentTargets (Cell cell){
        ArrayList<Player> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (cell.adiacency(direction) != Border.wall) {
                targets.addAll(getPlayersFromCell(getCellFromDirection(cell, direction)));
            }
        }
        return targets;
    }

    public ArrayList<Player> getSeenTargets(Player player) {
        ArrayList<Player> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if(playersPosition.get(player).adiacency(direction)==Border.door) {
                for (Cell cell : getRoomFromCell(getCellFromDirection(playersPosition.get(player), direction))) {
                    targets.addAll(getPlayersFromCell(cell));
                }
            }
        }
        for (Cell cell1 : getRoomFromCell(playersPosition.get(player))){
            targets.addAll(getPlayersFromCell(cell1));
        }
        targets.sort(Player::compareTo);
        targets.remove(player);
        return targets;
    }

    public ArrayList<Player> getUnseenTargets(Player player) {
        ArrayList<Player> targets = new ArrayList<>();
        ArrayList<Player> seenTargets = getSeenTargets(player);
        for(Player p : playersPosition.keySet())
            if (!(seenTargets.contains(p)) && p != player)
                targets.add(p);
        return targets;
    }

    public ArrayList<Player> getTargetsFromDirection(Player player, Direction direction) {
        ArrayList<Player> targets = new ArrayList<>();
        targets.addAll(getTargetsInMyCell(player));
        Cell cell = getCellFromDirection(playersPosition.get(player), direction);
        while(cell != null) {
            targets.addAll(getPlayersFromCell(cell));
            cell = getCellFromDirection(cell, direction);
        }
        targets.sort(Player::compareTo);
        return targets;
    }

    public boolean isAOneStepValidMove(Player player, Cell cell) {
        for (Direction direction : Direction.values()) {
            if (playersPosition.get(player).adiacency(direction) != Border.wall && getCellFromDirection(playersPosition.get(player), direction) == cell) {
                return true;
            }
        }
        return false;
    }

    public void setPlayerPosition (Player player, int i, int j){
        Cell cell = getCell(i,j);
        playersPosition.put(player, cell);
    }

    public void setPlayerPosition (Player player, Cell cell){
        playersPosition.put(player, cell);
    }

    public Cell getPositionFromPlayer (Player player) {
        return playersPosition.get(player);
    }

}