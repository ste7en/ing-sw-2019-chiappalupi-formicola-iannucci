package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Elena Iannucci
 */

public class GameMap {

    private Cell[][] map;
    private LinkedHashMap<Player, Cell> playersPosition;
    private int rows;
    private int columns;

    public GameMap(MapType mapType, LinkedHashMap<Player, Cell> playersPosition){
        rows=3;
        columns=4;
        this.playersPosition = new LinkedHashMap<>();
        this.playersPosition.putAll(playersPosition);
        this.map = new Cell[3][4];
        switch(mapType){
            case conf_4:
            map[0][0] = new Cell(Border.wall, Border.door, Border.space, Border.wall, CellColor.red,false , null );
            map[0][1] = new Cell(Border.wall, Border.space, Border.door, Border.door, CellColor.blue,false , null );
            map[0][2] = new Cell(Border.wall, Border.door, Border.door, Border.space, CellColor.blue, true, null );
            map[0][3] = new Cell(Border.wall, Border.wall, Border.door, Border.door, CellColor.green,false , null );
            map[1][0] = new Cell(Border.space, Border.wall, Border.door, Border.wall, CellColor.red,true , null );
            map[1][1] = new Cell(Border.door, Border.wall, Border.door, Border.wall, CellColor.pink,false , null );
            map[1][2] = new Cell(Border.door, Border.space, Border.space, Border.wall, CellColor.yellow,false , null );
            map[1][3] = new Cell(Border.door, Border.wall, Border.space, Border.space, CellColor.yellow,false , null );
            map[2][0] = new Cell(Border.door, Border.space, Border.wall, Border.wall, CellColor.white,false , null );
            map[2][1] = new Cell(Border.door, Border.door, Border.wall, Border.space, CellColor.white,false , null );
            map[2][2] = new Cell(Border.space, Border.space, Border.wall, Border.door, CellColor.yellow,false , null );
            map[2][3] = new Cell(Border.space, Border.wall, Border.wall, Border.space, CellColor.yellow,true , null );
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

    public Cell getCell(int i, int j) {
        return map[i][j];
    }


    public ArrayList<Cell> getRoomFromCell (Cell cell) {
        ArrayList<Cell> room = new ArrayList<>();
        int i,j;
        for (i=0; i<rows; i++) {
            for(j=0; j<columns; j++){
                if (map[i][j].getColor().equals(cell.getColor())) room.add(map[i][j]);
            }
        }
        return room;
    }

    private Cell getCellFromDirection (Player player, Direction direction){
        int i,j;
        for (i=0; i<rows; i++) {
            for(j=0; j<columns; j++) {
                if (map[i][j].equals(playersPosition.get(player))) {
                    switch (direction){
                        case North:
                            return map[i-1][j];
                        case East:
                            return map[i][j+1];
                        case South:
                            return map[i+1][j];
                        case West:
                            return map[i][j-1];
                    }
                }

            }
        }
        return null;
    }

    private ArrayList<Player> getPlayersFromCell (Cell cell){
        ArrayList<Player> playersInThatCell = new ArrayList<>();
        for (Player player : playersPosition.keySet()){
            if (playersPosition.get(player).equals(cell)) playersInThatCell.add(player);
        }
        return playersInThatCell;
    }


    public ArrayList<Player> getTargetsInMyCell (Player player){
        return getPlayersFromCell(playersPosition.get(player));
    }

    public ArrayList<Player> getOneMoveAwayTargets (Player player){
        ArrayList<Player> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
           if(playersPosition.get(player).adiacency(direction) != Border.wall) {
               targets.addAll(getPlayersFromCell(getCellFromDirection(player, direction)));
           }
        }
        return targets;
    }

    public ArrayList<Player> getTargetsFrom(Cell cell, Border border) {
        return null;

    }

    public ArrayList<Player> getTargetsFrom(Direction direction) { return null; }

    public void setPlayerPosition (Player player, int i, int j){
        Cell cell = getCell(i,j);
        playersPosition.put(player, cell);
    }

    public Cell getPositionFromPlayer (Player player) {
        return playersPosition.get(player);
    }
}